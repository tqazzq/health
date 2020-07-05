package com.itheima.health.dao;

import com.itheima.health.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:30 2020/6/30
 */
public interface OrderDao {
    void add(Order order);

    Map findById4Detail(Integer id);

    int findOrderCountByDate(String reportDate);

    int findVisitsCountByDate(String reportDate);

    int findOrderCountBetweenDate(@Param("monday") String monday,@Param("sunday") String sunday);

    int findVisitsCountAfterDate(String monday);

    List<Map<String, Object>> findHostSetmeal();

    int findOrderCountOfMonth(@Param("firstDayOfMonth") String firstDayOfMonth,@Param("lastDayOfMonth") String lastDayOfMonth);

    int findVisitsCountOfMonth(String firstDayOfMonth);
}
