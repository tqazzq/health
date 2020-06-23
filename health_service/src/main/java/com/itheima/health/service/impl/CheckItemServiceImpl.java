package com.itheima.health.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:24 2020/6/21
 */
@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;
    @Override
    public List<CheckItem> findAll() {
        List<CheckItem> list = (List<CheckItem>) checkItemDao.findAll();
        return list;

    }

    @Override
    //新增
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    //分页查询
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        //使用PageHelp这个方法 从queryPage中拿到当前页和currentPage和pageSize两个
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //模糊查询 拼接 %
        //判断是否有查询条件(对象中是否含有模糊查询的条件)
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            //有条件的话拼接
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");
        }
        //紧接着语句会被分页
        Page<CheckItem> page = checkItemDao.findByCondition(queryPageBean.getQueryString());
        PageResult<CheckItem> pageResult = new PageResult<CheckItem>(page.getTotal(),page.getResult());
        return pageResult;
    }

    @Override
    //根据id删除
    public void deleteById(Integer id) {
        //要判断 此检查项在citation业务中使用了没有 如果已经使用则不能删除
       int count =  checkItemDao.findCountByCheckItemId(id);
       if (count > 0){
           throw new HealthException(MessageConstant.DELETE_CHECKITEMID_IN_USE);
       }
       checkItemDao.deleteById(id);
    }

    @Override
    //根据id查询
    public CheckItem findById(Integer id) {
        CheckItem checkItem =  checkItemDao.findById(id);
        return checkItem;
    }

    @Override
    //修改检查项
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }
}
