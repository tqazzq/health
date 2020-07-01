package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author Tian Qing
 * @Daate: Created in 20:17 2020/7/1
 */
@Service(interfaceClass = LoginService.class)
public class LoginServiceImpl implements LoginService {
    @Autowired
    private MemberDao memberDao;
    @Override
    public Member findMemberByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void addMember(Member member) {
        memberDao.addMember(member);
    }
}
