package com.itheima.health.service;

import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.OrderSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 22:01 2020/6/27
 */
public interface OrderSettingService {

    void add(ArrayList<OrderSetting> orderSettingList);

    List<Map<String, Integer>> getOrderSettingByMonth(String month);

    void editNumberByDate(OrderSetting orderSetting) throws HealthException;
}
