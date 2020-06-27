package com.itheima.health.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.exception.HealthException;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //根据检查组Id清理原来关联表中检查组和检查项的关系
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //向关联表插入数据(建立新的关联关系)
        if (null != checkitemIds) {
            for (Integer checkitemId : checkitemIds) {
                checkGroupDao.setCheckGroupAndCheckItem(checkGroup.getId(), checkitemId);
            }
        }
        //更新检查组的基本信息
        checkGroupDao.edit(checkGroup);
    }

    @Override
    //删除检查组
    @Transactional
    public void deleteById(Integer id) {
        //删除检查组之前要先查看他有没有被套餐使用 ,如果已经在使用中了则不能删除,会导致业务的错乱 付款不正确等
        int count = checkGroupDao.findSetmealCountByCheckGroupId(id);
        //被使用了抛出异常
        if (count > 0) {
            throw new HealthException(MessageConstant.DELETE_CHECKGROUP_IN_USE);
        }
        //没有使用则先删除检查组与检查项的关系
        checkGroupDao.deleteCheckGroupCheckItem(id);
        //最后删除检查组
        checkGroupDao.deleteById(id);
    }

    @Override
    //查询所有检查组 在套餐中显示
    public List<CheckGroup> findAll() {
        List<CheckGroup> checkGroupList = checkGroupDao.findAll();
        return checkGroupList;
    }

    //向关联表插入数据 建立新的关联关系
//    private void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {
//        if (checkitemIds != null){
//            for (Integer checkitemId : checkitemIds) {
//                Map<String,Integer> map = new HashMap<>();
//                map.put("checkgroup_id",checkGroupId);
//                map.put("checkitem_id",checkitemId);
//                checkGroupDao.setCheckGroupAndCheckItem(map);
//            }
//        }
//    }
}
