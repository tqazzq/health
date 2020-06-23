package com.itheima.health.dao;

import com.itheima.health.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Tian Qing
 * @Daate: Created in 16:21 2020/6/23
 */
public interface CheckGroupDao {


    void add(CheckGroup checkGroup);

    //两个参数类型相同 要取别名不然分辨不出来
    void addChenkItemCheckGroup(@Param("checkGroupId") Integer checkGroupId,@Param("checkitemId") Integer checkitemId);
}
