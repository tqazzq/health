package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 19:21 2020/7/3
 */
@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;

    @Override
    public List<Integer> getMemberReport(List<String> months) {
        List<Integer> memberCount = new ArrayList<>();
        if (null != months){
            for (String month : months) {
                //查询这月有多少新增的会员
                Integer count =  memberDao.findMemberCountByDate(month + 31);
                memberCount.add(count);
            }
        }
        return memberCount;
    }
}
