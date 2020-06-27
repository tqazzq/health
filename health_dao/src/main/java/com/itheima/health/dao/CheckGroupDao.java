package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 16:21 2020/6/23
 */
public interface CheckGroupDao {

    public void add(CheckGroup checkGroup);

    //两个参数类型相同 要取别名不然分辨不出来
    public void addCheckItemCheckGroup(@Param("checkGroupId") Integer checkGroupId, @Param("checkitemId") Integer checkitemId);

    public Page<CheckGroup> findByCondition(String queryString);

    public CheckGroup findById(Integer id);

    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    public void edit(CheckGroup checkGroup);

    public void deleteAssociation(Integer id);

    public void setCheckGroupAndCheckItem(@Param("id") Integer id, @Param("checkitemId") Integer checkitemId);

    public void deleteById(Integer id);

    public Integer findSetmealCountByCheckGroupId(Integer id);

    public void deleteCheckGroupCheckItem(Integer id);

    public List<CheckGroup> findAll();
}
