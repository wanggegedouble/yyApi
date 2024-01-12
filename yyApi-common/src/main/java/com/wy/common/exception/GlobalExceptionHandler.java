package com.wy.common.exception;

import com.wy.common.result.RestResult;
import com.wy.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public RestResult<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException { } {}", e.getMessage(), e);
        return RestResult.of(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public RestResult<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException:{ }", e);
        return RestResult.of(ResultCode.SYSTEM_ERROR, e.getMessage());
    }
}
