package com.itheima.health.dao;

import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 21:30 2020/6/30
 */
public interface MemberDao {


    Member findByTelephone(String telephone);

    void addMember(Member member);

    List<Order> findOrderByMemberOrder(Order order);
}
