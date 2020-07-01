package com.itheima.health.service;

import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Order;

import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 18:52 2020/6/30
 */
public interface OrderService {
    Order submit(Map<String, Object> orderInfo) throws HealthException;
}
