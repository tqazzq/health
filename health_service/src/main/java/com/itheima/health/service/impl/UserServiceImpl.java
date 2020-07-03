package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.UserDso;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author Tian Qing
 * @Daate: Created in 20:14 2020/7/2
 */
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDso userDso;

    /**
     * 根据用户名查询用户权限
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        return userDso.findByUsername(username);
    }
}
