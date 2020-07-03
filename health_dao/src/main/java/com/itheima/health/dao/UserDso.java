package com.itheima.health.dao;

import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.User;

/**
 * @Author Tian Qing
 * @Daate: Created in 20:23 2020/7/2
 */
public interface UserDso {
    User findByUsername(String username) throws HealthException;
}
