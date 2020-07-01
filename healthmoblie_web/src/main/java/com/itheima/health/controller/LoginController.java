package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 19:10 2020/7/1
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Reference
    private LoginService loginService;

    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/check")
    public Result check(@RequestBody Map<String, Object> loginInfo, HttpServletResponse response) {
        //拿到前端的手机号和验证码
        String telephone = (String) loginInfo.get("telephone");
        String code = (String) loginInfo.get("validateCode");
        //从redis中取出验证码进行验证
        Jedis jedis = jedisPool.getResource();
        String codeInRedis = jedis.get(RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone);
        //redis中为空 或者不一致
        if (codeInRedis == null) {
            return new Result(false, MessageConstant.SEND_VALIDATECODE_NONE);
        }
        if (!code.equals(codeInRedis)) {
            return new Result(false, MessageConstant.IMPORT_VALIDATECODE_ERROR);
        }
        //清除验证码以免重复使用
        jedis.del(RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone);
        //调用service根据电话号码查询判断是否为会员
        Member member = loginService.findMemberByTelephone(telephone);
        //如果不是会员注册为新会员
        if (member == null) {
            member = new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            member.setRemark("通过手机快速注册");
            loginService.addMember(member);
        }
        //利用cookie进行跟踪
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        // 将cookie设置存一个月
        cookie.setMaxAge(60 * 60 * 24 * 30);
        // 记录访问的哪些网址
        cookie.setPath("/");//可以在cookie中跟踪记录访问了哪些套餐
        //添加cookie
        response.addCookie(cookie);
        //返回
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

}
