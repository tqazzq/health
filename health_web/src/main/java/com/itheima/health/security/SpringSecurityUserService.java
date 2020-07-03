package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author Tian Qing
 * @Daate: Created in 19:12 2020/7/2
 */
//@Component //可以在这里加入注解放入spring容器也可以 在xml中配置 还是在xml中配置比较好 不会爆红
public class SpringSecurityUserService implements UserDetailsService  {
    @Reference
    private UserService userService;

    /**
     * 提供登录用户的信息 : username password 权限集合
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户的权限,先查询用户表在通过用户和角色的关联表查询用户的角色,再通过角色和权限的关联表查询权限
        User user = userService.findByUsername(username);
        if (null != user){
            //获取密码
            String password = user.getPassword();
            //权限集合
            List<GrantedAuthority> authorities = new ArrayList<>();
            //授权
            //用户所拥有的角色
            SimpleGrantedAuthority sai = null;
            Set<Role> roles = user.getRoles();
            if (null != roles){
                //用角色的关键字授予角色
                for (Role role : roles) {
                    sai = new SimpleGrantedAuthority(role.getKeyword());
                    authorities.add(sai);
                    //查询角色所拥有的权限
                    Set<Permission> permissions = role.getPermissions();
                    if (null != permissions){
                        for (Permission permission : permissions) {
                            //授权
                            sai = new SimpleGrantedAuthority(permission.getKeyword());
                            authorities.add(sai);
                        }
                    }
                }
            }
            return new org.springframework.security.core.userdetails.User(username,password,authorities);
        }
        //如果没有权限 返回null 限制访问
        return null;
    }
}
