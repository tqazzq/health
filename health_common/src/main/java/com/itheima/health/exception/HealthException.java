package com.itheima.health.exception;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:10 2020/6/22
 */
public class HealthException extends RuntimeException {
    /*
    * 自定义异常
    * 友好提示
    * 终止不符合业务逻辑的代码
    * */
    public HealthException(String message){
        super(message);
    }
}
