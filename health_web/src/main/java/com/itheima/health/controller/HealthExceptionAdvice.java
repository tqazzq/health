package com.itheima.health.controller;

import com.itheima.health.entity.Result;
import com.itheima.health.exception.HealthException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:19 2020/6/22
 */
@RestControllerAdvice
public class HealthExceptionAdvice {
    /*
     * 自定义异常处理
     *
     * */
    @ExceptionHandler(HealthException.class)
    public Result handleHealthException(HealthException he) {
        return new Result(false, he.getMessage());
    }

    /*
     * 其他所有未知异常
     * */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception he) {
        return new Result(false, "网络繁忙");
    }
}
