package com.itheima.health.service;

import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckGroup;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 16:16 2020/6/23
 */
public interface CheckGroupService {

    public void add(CheckGroup checkGroup, Integer[] checkitemIds);

    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean);

    public CheckGroup findById(Integer id);

    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    public void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    public void deleteById(Integer id) throws HealthException;

    public List<CheckGroup> findAll();
}
