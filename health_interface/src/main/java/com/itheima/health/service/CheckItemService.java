package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
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

    PageResult<CheckItem> findPage(QueryPageBean queryPageBean);

    void deleteById(Integer id) throws HealthException;

    CheckItem findById(Integer id);

    void update(CheckItem checkItem);
}
