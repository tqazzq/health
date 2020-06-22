package com.itheima.health.dao;


import com.itheima.health.pojo.CheckItem;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:12 2020/6/21
 */
public interface CheckItemDao {
    public List<CheckItem> findAll();

    void add(CheckItem checkItem);
}
