package com.itheima.health.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 16:17 2020/6/23
 */
@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override
    @Transactional
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //添加检查组还要添加他们自己的关系表
        //先添加检查组
        checkGroupDao.add(checkGroup);
        //获取检查组的id
        Integer checkGroupId = checkGroup.getId();
        //遍历前端传过来的检查项id 并添加两者的关系
        if (null != checkitemIds) {
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.addCheckItemCheckGroup(checkGroupId, checkitemId); //一个检查组对应多个检查项
            }
        }
    }

    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        //使用Mybatis分页插件PageHelper
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        //判断是否有条件 有条件就进行模糊查询
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())) {
            //拼接
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString() + "%");
        }
        //分页
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryPageBean.getQueryString());
        return new PageResult<CheckGroup>(page.getTotal(), page.getResult());

    }

    @Override
    //根据id查询检查组信息
    public CheckGroup findById(Integer id) {
        CheckGroup checkGroup = checkGroupDao.findById(id);
        return checkGroup;
    }

    @Override
    //查询检查组包含的检查项的信息
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        List<Integer> list = checkGroupDao.findCheckItemIdsByCheckGroupId(id);
        return list;
    }

    @Override
    //修改检查组的信息 同事还要更新和检查项的关联表
    @Transactional
    public void edit(CheckGroup checkGroup, Integer[] ids) {
        //根据检查组Id清理原来关联表中检查组和检查项的关系
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //向关联表插入数据(建立新的关联关系)
        setCheckGroupAndCheckItem(checkGroup.getId(), ids);
        //更新检查组的基本信息
        checkGroupDao.edit(checkGroup);
    }

    //向关联表插入数据 建立新的关联关系
    private void setCheckGroupAndCheckItem(Integer id, Integer[] ids) {
        if (ids != null){
            for (Integer checkitemId : ids) {
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroup_id",id);
                map.put("checkitem_id",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
