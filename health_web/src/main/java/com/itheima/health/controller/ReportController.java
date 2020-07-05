package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.ReportService;
import com.itheima.health.service.SetmealService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Tian Qing
 * @Daate: Created in 18:51 2020/7/3
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    @GetMapping("/getMemberReport")
    public Result getMemberReport() {
//        组装过去12个月的数据,前端是一个数组
        List<String> months = new ArrayList<>();
//        使用java中的calendar来操作日期
        Calendar calendar = Calendar.getInstance();
//        减去一年
        calendar.add(calendar.MONTH, -12);
//        设置日期格式yyyy-MM
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//       for循环 构建12个月的数据
        for (int i = 0; i < 12; i++) {
            //每次增加一个月
            calendar.add(calendar.MONTH, 1);
            //获得日期格式
            Date date = calendar.getTime();
            //加入集合中
            months.add(sdf.format(date));
        }
//        调用服务查询过去12个月会员数据 前端也是一数组 (数值 代表多少个)
        List<Integer> memberCount = memberService.getMemberReport(months);
//        放入map
        Map<String, Object> map = new HashMap<>();
        map.put("months", months);
        map.put("memberCount", memberCount);
//        返回
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    /**
     * 查询各预约套餐的占比
     *
     * @return
     */
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport() {
        //调用服务查询 套餐数量 返回的数据可是为 list<Map<String,Object> Object存贮着套餐的名称 转存入setmealNames 集合中
        List<Map<String, Object>> setmealCount = setmealService.findSetmealCount();
        //套餐名称集合
        ArrayList<Object> setmealNames = new ArrayList<>();
        //抽取套餐名称
        if (null != setmealCount) {
            for (Map<String, Object> map : setmealCount) {
                setmealNames.add(((String) map.get("name")));
            }
        }
        //封装返回的结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("setmealNames", setmealNames);
        resultMap.put("setmealCount", setmealCount);
        return new Result(true, "套餐占比查询成功", resultMap);
    }

    /**
     * 运营数据统计
     *
     * @return
     */
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        Map<String, Object> businessReport = reportService.getBusinessReportDate();
        return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, businessReport);
    }

    /**
     * 导出运营统计的excel表
     *
     * @return
     */
    @RequestMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest req, HttpServletResponse res) {
        // 获取模板的路径, getRealPath("/") 相当于到webapp目录下
        String template = req.getSession().getServletContext().getRealPath("/template/report_template.xlsx");
        // 创建工作簿(模板路径)
        try (// 写在try()里的对象，必须实现closable接口，try()cathc()中的finally
             OutputStream os = res.getOutputStream();
             XSSFWorkbook wk = new XSSFWorkbook(template)) {
            // 获取工作表
            XSSFSheet sht = wk.getSheetAt(0);
            // 获取运营统计数据
            Map<String, Object> reportData = reportService.getBusinessReportDate();
            // 日期 坐标 2,5
            sht.getRow(2).getCell(5).setCellValue(reportData.get("reportDate").toString());
            //======================== 会员 ===========================
            // 新增会员数 4,5
            sht.getRow(4).getCell(5).setCellValue((Integer) reportData.get("todayNewMember"));
            // 总会员数 4,7
            sht.getRow(4).getCell(7).setCellValue((Integer) reportData.get("totalMember"));
            // 本周新增会员数5,5
            sht.getRow(5).getCell(5).setCellValue((Integer) reportData.get("thisWeekNewMember"));
            // 本月新增会员数 5,7
            sht.getRow(5).getCell(7).setCellValue((Integer) reportData.get("thisMonthNewMember"));

            //=================== 预约 ============================
            sht.getRow(7).getCell(5).setCellValue((Integer) reportData.get("todayOrderNumber"));
            sht.getRow(7).getCell(7).setCellValue((Integer) reportData.get("todayVisitsNumber"));
            sht.getRow(8).getCell(5).setCellValue((Integer) reportData.get("thisWeekOrderNumber"));
            sht.getRow(8).getCell(7).setCellValue((Integer) reportData.get("thisWeekVisitsNumber"));
            sht.getRow(9).getCell(5).setCellValue((Integer) reportData.get("thisMonthOrderNumber"));
            sht.getRow(9).getCell(7).setCellValue((Integer) reportData.get("thisMonthVisitsNumber"));

            // 热门套餐
            List<Map<String, Object>> hotSetmeal = (List<Map<String, Object>>) reportData.get("hotSetmeal");
            int row = 12;
            for (Map<String, Object> setmealMap : hotSetmeal) {
                sht.getRow(row).getCell(4).setCellValue((String) setmealMap.get("name"));
                sht.getRow(row).getCell(5).setCellValue((Long) setmealMap.get("setmeal_count"));
                BigDecimal proportion = (BigDecimal) setmealMap.get("percentage");
                sht.getRow(row).getCell(6).setCellValue(proportion.doubleValue());
                sht.getRow(row).getCell(7).setCellValue((String) setmealMap.get("remark"));
                row++;
            }

            // 工作簿写给reponse输出流
            res.setContentType("application/vnd.ms-excel");
            String filename = "运营统计数据报表.xlsx";
            // 解决下载的文件名 中文乱码
            filename = new String(filename.getBytes(), "ISO-8859-1");
            // 设置头信息，告诉浏览器，是带附件的，文件下载
            res.setHeader("Content-Disposition", "attachement;filename=" + filename);
            wk.write(os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/exportPdfBusinessReportPdf")
    public Result exportPdfBusinessReportPdf(HttpServletRequest request, HttpServletResponse response){
        //获取模板的路径
        String basePath = request.getSession().getServletContext().getRealPath("/template");
        //jrxml路径
        String jrxml = basePath + File.separator + "health_business3.jrxml";
        //jasper的输出路径
        String jasper = basePath + File.separator + "report_business.jasper";
        try {
            JasperCompileManager.compileReportToFile(jrxml, jasper);
            Map<String,Object> businessReport = reportService.getBusinessReportDate();
            //热门套餐
            List<Map<String,Object>> hotSetmeals = (List<Map<String, Object>>) businessReport.get("hotSetmeal");
            //填充数据
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, businessReport, new JRBeanCollectionDataSource(hotSetmeals));
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition","attachement;filename=businessReport.pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
            return null;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"导出运营统计数据失败");
    }
}
