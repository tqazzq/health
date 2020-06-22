package com.itheima.health.service;

import com.itheima.health.pojo.CheckItem;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:01 2020/6/21
 */
public interface CheckItemService {
    //查询所有
    public List<CheckItem> findAll();

    void add(CheckItem checkItem);
}
