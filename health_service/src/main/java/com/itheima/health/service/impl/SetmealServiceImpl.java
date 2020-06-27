package com.itheima.health.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:31 2020/6/25
 */
@Service(interfaceClass = SetmealService.class)
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        //获取套餐的id
        Integer setmealId = setmeal.getId();
        //添加套餐和检查组的关系
        if (null != setmealId) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmealId, checkgroupId);
            }
        }
    }

    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        //根据添加模糊查询
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) { //如果有搜索的查询 则进行模糊查询
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");// 在原有的查询语句上加上模糊匹配的%
        }
        //条件查询
        Page<Setmeal> page = setmealDao.findByCondition(queryPageBean.getQueryString());
        //返回结果集和总记录数
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public Setmeal findById(Integer id) {
        Setmeal setmeal = setmealDao.findById(id);
        return setmeal;
    }

    @Override
    public List<Integer> findCheckgroupIdsBySetmealId(Integer id) {

        List<Integer> list = setmealDao.findCheckgroupIdsBySetmealId(id);
        return list;
    }

    @Override
    @Transactional
    public void update(Setmeal setmeal, Integer[] checkgroupIds) {
        //先更新套餐信息
        setmealDao.update(setmeal);
        //删除酒管系
        setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        //建立新关系
        if (null != checkgroupIds) {
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(), checkgroupId);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        //先检查套餐有没有被订单使用,如果被使用了则不能删除
        int count = setmealDao.findOrderCountBySetmealId(id);
        if (count > 0) {
            throw new HealthException(MessageConstant.DELETE_STMEAL_IN_USE);
        }
        //没有被使用的话先删除套餐和检查组的关系再删除套餐
        setmealDao.deleteSetmealCheckGroup(id);
        setmealDao.deleteSetmeal(id);
    }

}
