package com.wy.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-13  10:21
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class RestResult<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    public RestResult(String message, T data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.message = message;
        this.data = data;
    }

    public RestResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public static<T> RestResult<T> of(String message, T data) {
        return new RestResult<>(message,data);
    }

    public static<T> RestResult<T> of(int code, String message) {
        return new RestResult<>(code,message);
    }

    public static<T> RestResult<T> of(ResultCode resultCode,String message) {
        if (resultCode == null) {
            resultCode = ResultCode.SUCCESS;
        }
        return new RestResult<>(resultCode.getCode(),message);
    }

    public static<T> RestResult<T> of(String message) {
        ResultCode success = ResultCode.SUCCESS;
        return new RestResult<>(success.getCode(),message);
    }
}
