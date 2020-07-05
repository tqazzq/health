package com.itheima.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @Author Tian Qing
 * @Daate: Created in 11:43 2020/7/2
 */
@Component
public class UserServiceImpl implements UserDetailsService {

    /**
     * 通过用户名加载用户信息
     * user用于登录
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
