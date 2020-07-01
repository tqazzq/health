package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 22:02 2020/6/27
 */
@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;


    @Override
    @Transactional
    public void add(ArrayList<OrderSetting> orderSettingList) {
        for (OrderSetting orderSetting : orderSettingList) {
            OrderSetting osDb = orderSettingDao.findByorderSettingDate(orderSetting.getOrderDate());
            //通过日期来查询数据库中是否已经存在这个设置
            if (null != osDb) {
                //存在的话判断新上传的数据的预约人数是否大于了数据库的可预约人数,要是大于直接报错
                if (osDb.getReservations() > orderSetting.getNumber()) {
                    throw new HealthException(orderSetting.getOrderDate() + "中已预约数量不能大于可预约数量");
                }
                //没有大于就修改数据
                orderSettingDao.updateNumber(orderSetting);
            } else {
                //不存在这个数据就直接添加
                orderSettingDao.add(orderSetting);
            }
        }
    }

    @Override
    //根据日期查询预约设置的数据
    public List<Map<String, Integer>> getOrderSettingByMonth(String date) {
//        老版本 有点坑老版本
//        //传过来的数据只有年份和月份
//        //月份拼接日期 拼接1 号和31号
//        String dateBegin = date + "-1";
//        String dateEnd = date + "-31";
//        Map map = new HashMap();
//        map.put("dateBegin", dateBegin);
//        map.put("dateEnd", dateEnd);
//        //查询当前月份的预约设置
//        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
////        将出巡出来的信息存入一个list<Map>的格式
//        List<Map> data = new ArrayList<>();
//        for (OrderSetting orderSetting : list) {
        //    Map orderSettingMap = new HashMap();
//            String s = new SimpleDateFormat("dd").format(orderSetting.getOrderDate());
//            orderSettingMap.put("date", s);
//            orderSettingMap.put("number", orderSetting.getNumber());
//            orderSettingMap.put("reservations", orderSetting.getReservations());
//            data.add(orderSettingMap);
//        }
//
//        return data;
        String dateBegin = date + "-1";
        String dateEnd = date + "-31";
        List<Map<String, Integer>> list = orderSettingDao.getOrderSettingByMonth(dateBegin, dateEnd);
        return list;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //判断是否存在通过日期来查询
        OrderSetting osDB = orderSettingDao.findByorderSettingDate(orderSetting.getOrderDate());
        if (null != osDB){
            if (osDB.getReservations() > orderSetting.getNumber()){
                String s = new SimpleDateFormat("MM-dd").format(orderSetting.getOrderDate());
                throw new HealthException(s  + "号中已预约数量不能大于可预约数量");
            }
            orderSettingDao.updateNumber(orderSetting);
        }else {//不存在的话直接添加
            orderSettingDao.add(orderSetting);
        }

    }
}
