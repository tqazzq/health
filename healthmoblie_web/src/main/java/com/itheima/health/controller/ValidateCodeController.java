package com.itheima.health.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.utils.SMSUtils;
import com.itheima.health.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author Tian Qing
 * @Daate: Created in 17:02 2020/6/30
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;

    @PostMapping("/send4Order")
    public Result send4Order(String telephone) {
        //redis的验证码的存储格式 002telephone = code
        //发送验证码之前先通过redis缓存查看验证码是否已经发送
        Jedis jedis = jedisPool.getResource();
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        String codeInRedis = jedis.get(key);
        //没有发送则发送验证码
        if (null == codeInRedis) {
            Integer code = ValidateCodeUtils.generateValidateCode(6);
            try {
                //把验证码加上手机号存入redis中在设置存活时间
                SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code + "");
                jedis.setex(key, 15 * 60, code + "");
                //返回成功
                return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
            } catch (ClientException e) {
                e.printStackTrace();
                //捕获异常的话返回失败
                return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
            }
        }
        //如果redis中已经存在号码了直接返回信息提示验证码已经发送
        return new Result(false, MessageConstant.SENT_VALIDATECODE);
    }
}
