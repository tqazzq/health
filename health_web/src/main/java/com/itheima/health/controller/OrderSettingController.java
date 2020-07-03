package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import com.itheima.health.utils.POIUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:14 2020/6/27
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;

    //文件上传
    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile) {
        try {
            List<String[]> strings = POIUtils.readExcel(excelFile);
            //转成list
            ArrayList<OrderSetting> orderSettingList = new ArrayList<>();
            //从工具类中获取定义日期格式
            SimpleDateFormat sdf = new SimpleDateFormat(POIUtils.DATE_FORMAT);
            Date orderDate = null;
            OrderSetting os = null;
            //遍历数据
            for (String[] string : strings) {
                //格式化日期数据
                orderDate = sdf.parse(string[0]);
                //讲数值数据(人数)转为Int
                int number = Integer.valueOf(string[1]);
                //讲转化后的数据设置到实体类orderSetting中
                os = new OrderSetting(orderDate, number);
                //添加到集合
                orderSettingList.add(os);
            }
            //调用服务
            orderSettingService.add(orderSettingList);
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }

    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(@RequestParam(name = "month", required = false) String date) {
        // 调用服务查询月份数据
        List<Map<String,Integer>> monthData = orderSettingService.getOrderSettingByMonth(date);
        // 返回给前端
        return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,monthData);
    }

    @PostMapping("/editNumberByDate")

    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        orderSettingService.editNumberByDate(orderSetting);
        return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
    }
}
