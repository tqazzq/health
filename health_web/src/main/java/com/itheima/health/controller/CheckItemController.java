package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.service.CheckItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 20:55 2020/6/21
 */
@RestController
@RequestMapping("/checkitem")
public class CheckItemController {

    @Reference
    private CheckItemService checkItemSerevice;

    @GetMapping("/findAll")
    //查询所有
    public Result findAll(){
        List<CheckItem> list = (List<CheckItem>) checkItemSerevice.findAll();
        return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
    }

    //新增
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){
        try {
            checkItemSerevice.add(checkItem);
        } catch (Exception e) {
            return new Result(false,MessageConstant.ADD_CHECKITEM_FAIL);
        }
            return new Result(true,MessageConstant.ADD_CHECKITEM_SUCCESS);
    }
}
