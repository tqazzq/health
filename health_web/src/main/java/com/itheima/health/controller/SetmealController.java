package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import org.aspectj.bridge.Message;
import org.omg.CORBA.TRANSACTION_MODE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:45 2020/6/25
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;

    //上传图片
    @RequestMapping("/upload")
    public Result upload(MultipartFile imgFile) {
        String filename = imgFile.getOriginalFilename();
        //获取图片的后缀名
        String ext = filename.substring(filename.lastIndexOf("."));
        //生成唯一文件名
        String uniqueName = UUID.randomUUID().toString().replace("-", "") + ext;
        Jedis jedis = jedisPool.getResource();
        //调用工具类上传到七牛
        try {
            QiNiuUtils.uploadViaByte(imgFile.getBytes(), uniqueName);
            //保存所有上传的图片到redis集合中
            jedis.sadd(RedisConstant.SETMEAL_PIC_RESOURCES,uniqueName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }finally {
            if (null != jedis)
                jedis.close();
        }
        //成功返回数据给前端
        /*格式如下
        {
        flag:
        message:
        data:{
        imgName:图片名称
        domain: 七牛的域名
        * */
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("imgName", uniqueName);
        dataMap.put("domain", QiNiuUtils.DOMAIN);
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, dataMap);
    }

    @PostMapping("/add")
    //添加套餐
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds) {
        Jedis jedis = jedisPool.getResource();
        setmealService.add(setmeal, checkgroupIds);
        //套餐添加成功,把图片存入redis另一个Key中
        jedis.sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        jedis.close();
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @PostMapping("/findPage")
    public Result findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult<Setmeal> pageResult = setmealService.findPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, pageResult);
    }

    @GetMapping("/findById")
    public Result findById(Integer id) {
        Setmeal setmeal = setmealService.findById(id);
        //前端要回显图片就需要全路劲,数据库只是存贮了图片名称 所以需要setmeal.setimg= (QiNiuUtils.DOMAIN + setmeal.getImg()) 来获取图片的全路径
        //将返回的数据存到map中
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("setmeal",setmeal);
        resultMap.put("imageUrl",QiNiuUtils.DOMAIN + setmeal.getImg());
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,resultMap);
    }


    //查询套餐所包含的检查组id
    @GetMapping("/findCheckgroupIdsBySetmealId")
    public Result findCheckgroupIdsBySetmealId(Integer id){
        List<Integer> checkgroupIds = setmealService.findCheckgroupIdsBySetmealId(id);
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkgroupIds);
    }

    //修改
    @PostMapping("/update")
    public Result update(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        Jedis jedis = jedisPool.getResource();
        //旧的套餐数据
        Setmeal oldSetmeal = setmealService.findById(setmeal.getId());
        //调用业务修改
        setmealService.update(setmeal,checkgroupIds);
        //从redis中删除旧图片
        jedis.srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,oldSetmeal.getImg());
        //添加新图片即使 是重复图片set集合也不会存入存入重复的图片
        jedis.sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        jedis.close();
        return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        //查询要删除的套餐图片的名称
        Setmeal setmeal = setmealService.findById(id);
        setmealService.delete(id);
        Jedis jedis = jedisPool.getResource();
        //从redis中删除旧图片
        jedis.srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }
}
