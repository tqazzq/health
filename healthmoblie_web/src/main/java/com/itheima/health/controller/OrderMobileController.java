package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 18:44 2020/6/30
 */
@RestController
@RequestMapping("/order")
public class OrderMobileController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String, Object> orderInfo) {
        if (orderInfo.get("telephone") == null || orderInfo.get("validateCode") == null || orderInfo.get("idCard") == null || orderInfo.get("orderDate") == null) {
            return new Result(false, "输入不能为空");
        }
        //从前端拿到手机号根据手机号在redis中获取验证码
        String telephone = (String) orderInfo.get("telephone");
        Jedis jedis = jedisPool.getResource();
        String codeInRedis = jedis.get(RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone);
        //如果redis中的的验证码不存在(两种可能 过期或者不存在)
        if (null == codeInRedis) {
            //返回前端并提示
            return new Result(false, MessageConstant.SEND_VALIDATECODE_NONE);
        }
        //如果不一致
        if (!orderInfo.get("validateCode").equals(codeInRedis)) {
            //返回验证码输出不正确
            return new Result(false, "验证码不一致,请核对后输入");
        }
        //都通过则验证码正确 清除redis中的验证码以免重复使用
        jedis.del(codeInRedis);
        //设置预约类型 提前设置预约类型
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
        //如果一致调用service查询
        Order order = orderService.submit(orderInfo);
        //返回正确的提示
        return new Result(true, MessageConstant.ORDER_SUCCESS, order);
    }

    @GetMapping("/findById")
    public Result findById(Integer id) {
       Map<String,Object> orderInfo = orderService.findById4Detail(id);
        return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
}
