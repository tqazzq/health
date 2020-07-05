package com.itheima.health.service;

import java.util.List;

/**
 * @Author Tian Qing
 * @Daate: Created in 19:20 2020/7/3
 */
public interface MemberService {
    List<Integer> getMemberReport(List<String> months);
}
