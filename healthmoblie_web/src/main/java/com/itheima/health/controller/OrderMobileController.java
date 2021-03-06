package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.DateUtils;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.awt.*;
import java.util.Date;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 18:44 2020/6/30
 */
@RestController
@RequestMapping("/order")
public class OrderMobileController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @Reference
    private SetmealService setmealService;


    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String, Object> orderInfo) {
        if (orderInfo.get("telephone") == null || orderInfo.get("validateCode") == null || orderInfo.get("idCard") == null || orderInfo.get("orderDate") == null) {
            return new Result(false, "输入不能为空");
        }
        //从前端拿到手机号根据手机号在redis中获取验证码
        String telephone = (String) orderInfo.get("telephone");
        Jedis jedis = jedisPool.getResource();
        String codeInRedis = jedis.get(RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone);
        //如果redis中的的验证码不存在(两种可能 过期或者不存在)
        if (null == codeInRedis) {
            //返回前端并提示
            return new Result(false, MessageConstant.SEND_VALIDATECODE_NONE);
        }
        //如果不一致
        if (!orderInfo.get("validateCode").equals(codeInRedis)) {
            //返回验证码输出不正确
            return new Result(false, "验证码不一致,请核对后输入");
        }
        //都通过则验证码正确 清除redis中的验证码以免重复使用
        jedis.del(codeInRedis);
        //设置预约类型 提前设置预约类型
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
        //如果一致调用service查询
        Order order = orderService.submit(orderInfo);
        //返回正确的提示
        return new Result(true, MessageConstant.ORDER_SUCCESS, order);
    }

    @GetMapping("/findById")
    public Result findById(Integer id) {
        Map<String, Object> orderInfo = orderService.findById4Detail(id);
        return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, orderInfo);
    }

    /**
     * 导出预约套餐pdf信息
     *
     * @param id
     * @param res
     * @return
     */
    @GetMapping("/exportSetmealInfo")
    public Result exportSetmealInfo(Integer id, HttpServletResponse res) {
        final Map<String, Object> orderInfo = orderService.findById4Detail(id);
        //套餐详情
        Integer setmeal_id = (Integer) orderInfo.get("setmeal_id");
        Setmeal setmeal = setmealService.findDetailById(setmeal_id);
        Document doc = new Document();
        // 保存到输出流
        try {
            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition", "attachement;filename=setmealInfo.pdf");
            PdfWriter.getInstance(doc, res.getOutputStream());
            // 打开文档
            doc.open();
            // 写内容
            // 设置表格字体
            BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);

            doc.add(new Paragraph("体检人: " + (String) orderInfo.get("member"), font));
            doc.add(new Paragraph("体检套餐: " + (String) orderInfo.get("setmeal"), font));
            Date orderDate = (Date) orderInfo.get("orderDate");
            doc.add(new Paragraph("体检日期: " + DateUtils.parseDate2String(orderDate, "yyyy-MM-dd"), font));
            doc.add(new Paragraph("预约类型: " + (String) orderInfo.get("orderType"), font));

            // 套餐详情
            Table table = new Table(3); // 3列  表头

            //======================== 表格样式 ========================
            // 向document 生成pdf表格
            table.setWidth(80); // 宽度
            table.setBorder(1); // 边框
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); //水平对齐方式
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式
            /*设置表格属性*/
            table.setBorderColor(new Color(0, 0, 255)); //将边框的颜色设置为蓝色
            table.setPadding(5);//设置表格与字体间的间距
            //table.setSpacing(5);//设置表格上下的间距
            table.setAlignment(Element.ALIGN_CENTER);//设置字体显示居中样式

            table.addCell(buildCell("项目名称", font));
            table.addCell(buildCell("项目内容", font));
            table.addCell(buildCell("项目解读", font));

            // 检查组
            List<CheckGroup> checkGroups = setmeal.getCheckGroups();
            if (null != checkGroups) {
                for (CheckGroup checkGroup : checkGroups) {
                    // 项目名称列
                    table.addCell(buildCell(checkGroup.getName(), font));
                    // 项目内容, 把所有的检查项拼接
                    List<CheckItem> checkItems = checkGroup.getCheckItems();
                    String checkItemStr = "";
                    if (null != checkItems) {
                        StringBuilder sb = new StringBuilder();
                        for (CheckItem checkItem : checkItems) {
                            sb.append(checkItem.getName()).append(" ");
                        }
                        sb.setLength(sb.length() - 1); // 去最一个空格
                        // 检查项的拼接完成
                        checkItemStr = sb.toString();
                    }
                    table.addCell(buildCell(checkItemStr, font));
                    // 项目解读
                    table.addCell(buildCell(checkGroup.getRemark(), font));
                }
            }
            // 添加表格
            doc.add(table);
            // 关闭文档
            doc.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "导出预约信息失败");
    }

    // 传递内容和字体样式，生成单元格
    private Cell buildCell(String content, Font font)
            throws BadElementException {
        Phrase phrase = new Phrase(content, font);
        return new Cell(phrase);
    }
}
