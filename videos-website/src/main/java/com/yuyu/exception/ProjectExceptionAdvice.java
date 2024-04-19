package com.yuyu.exception;

import com.yuyu.exception.BussinessException;
import com.yuyu.exception.Code;
import com.yuyu.exception.SystemException;
import com.yuyu.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.ExecutionException;

@RestControllerAdvice()
@Slf4j

/**
 * 定义全局异常类，用于异常的捕获与消息的记录
 */
public class ProjectExceptionAdvice {

    @ExceptionHandler(BussinessException.class)
    public Result doBusinessException(BussinessException exception){
        log.error(exception.getMessage());
        return new Result(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException exception){
        return new Result(exception.getCode(), exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result doException(Exception exception){
        // 记录日志
        log.error(exception.getMessage());
        return new Result(Code.SYSTEM_UNKNOW_ERROR,"系统繁忙，请联系管理员！");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result doException(AuthenticationException exception){
        // 记录日志
        log.error(exception.getMessage());
        return new Result(Code.AUTHENTICATION_ERROR,exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result doException(AccessDeniedException exception){
        // 记录日志
        log.error(exception.getMessage());
        return new Result(Code.ACCESSDENIED_ERROR,"权限不足！");
    }

}
