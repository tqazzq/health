package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 17:59 2020/6/28
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealMoblieController {
    @Reference
    private SetmealService setmealService;

    //查询所有
    @RequestMapping("/getSetmeal")
    public Result getSetmeal() {
        List<Setmeal> list = setmealService.findAll();
        //每当前端要展示图片记得拼接全路径因为 数据库是只保存了 图片的名称没有保存全路径的直接查返回是不行的
        list.forEach(s -> {
            s.setImg(QiNiuUtils.DOMAIN + s.getImg());
        });
        return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS, list);
    }

    @GetMapping("/findDetailById")
    public Result findDetailById(Integer id) {
        //调用服务查询
        Setmeal setmeal = setmealService.findDetailById(id);
        //设置图片完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }

    @GetMapping("/findById")
    public Result findById(Integer id){
        Setmeal setmeal = setmealService.findById(id);
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }
}
