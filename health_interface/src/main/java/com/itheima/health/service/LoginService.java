package com.itheima.health.service;

import com.itheima.health.pojo.Member;

/**
 * @Author Tian Qing
 * @Daate: Created in 20:17 2020/7/1
 */
public interface LoginService {
    Member findMemberByTelephone(String telephone);

    void addMember(Member member);
}
