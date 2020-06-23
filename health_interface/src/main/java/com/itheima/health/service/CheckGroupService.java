package com.itheima.health.service;

import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckGroup;

/**
 * @Author Tian Qing
 * @Daate: Created in 16:16 2020/6/23
 */
public interface CheckGroupService {

    public void add(CheckGroup checkGroup, Integer[] checkitemIds);
}
