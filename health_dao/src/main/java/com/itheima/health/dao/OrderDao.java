package com.itheima.health.dao;

import com.itheima.health.pojo.Order;

import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:30 2020/6/30
 */
public interface OrderDao {
    void add(Order order);

    Map findById4Detail(Integer id);
}
