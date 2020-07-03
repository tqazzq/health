package com.itheima.health.service;

import com.itheima.health.pojo.User;

/**
 * @Author Tian Qing
 * @Daate: Created in 20:14 2020/7/2
 */
public interface UserService {
    User findByUsername(String username);
}
