package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:33 2020/6/25
 */
public interface SetmealDao {

    void add(Setmeal setmeal);

//    因为参数类型一样mybatis分辨不出来要加上@param
    void addSetmealCheckGroup(@Param("setmealId") Integer setmealId,@Param("checkgroupId") Integer checkgroupId);

    Page<Setmeal> findByCondition(String queryString);

    Setmeal findById(Integer id);

    List<Integer> findCheckgroupIdsBySetmealId(Integer id);

    void update(Setmeal setmeal);

    void deleteSetmealCheckGroup(Integer id);

    int findOrderCountBySetmealId(Integer id);

    void deleteSetmeal(Integer id);

    List<Setmeal> findAll();

    Setmeal findByDetailById(Integer id);

    List<Map<String, Object>> findSetmealCount();
}
