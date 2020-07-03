package com.itheima.health.controller;

import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Tian Qing
 * @Daate: Created in 12:56 2020/7/3
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/getLoginUsername")
    public Result getLoginUsername(){
        String username = null;
        try {
            //获取用户登录的认证信息
//            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //登录用户名
            username = loginUser.getUsername();
            //返回给前端
        } catch (Exception e) {
            e.printStackTrace();
            throw new HealthException("获取用户名失败");
        }
        return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,username);
    }
}
