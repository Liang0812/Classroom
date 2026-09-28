package com.classroom.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /** 未匹配到任何接口/资源：返回 404，而不是 500 系统异常 */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public Result<Void> handleNoResource(Exception e) {
        return Result.error(ResultCode.NOT_FOUND, "接口不存在");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        String msg = e.getMessage();
        // 兼容其它版本 Spring 抛出的"无端点/无静态资源"异常（消息特征兜底）
        if (msg != null && (msg.startsWith("No endpoint") || msg.startsWith("No static resource"))) {
            return Result.error(ResultCode.NOT_FOUND, "接口不存在");
        }
        return Result.error(ResultCode.ERROR, "系统异常：" + msg);
    }
}
