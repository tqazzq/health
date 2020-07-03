package com.itheima.health.controller;

import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:19 2020/6/22
 */
@RestControllerAdvice
public class HealthExceptionAdvice {
    /**
     * 自定义异常处理
     * @param he
     * @return
     */
    @ExceptionHandler(HealthException.class)
    public Result handleHealthException(HealthException he) {
        return new Result(false, he.getMessage());
    }

    /**
     * 其他所有未知异常
     * @param he
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception he) {
        return new Result(false, "网络繁忙");
    }

    /**
     * 密码错误
     * @param he
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result BadCredentialsException(BadCredentialsException he){
        return handleUserPassword();
    }

    /**
     * 用户名不存在
     * @param he
     * @return
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public Result handleInternalAuthenticationServiceException(InternalAuthenticationServiceException he){
        return handleUserPassword();
    }

    private Result handleUserPassword() {
        return new Result(false,"用户名或者密码错误");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException he){
        return new Result(false,"没有权限");
    }
}
