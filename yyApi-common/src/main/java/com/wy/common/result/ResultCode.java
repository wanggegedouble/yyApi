package com.wy.common.result;

import lombok.Getter;

@Getter
public  enum ResultCode {
    SUCCESS(2000, "成功"),
    SYSTEM_ERROR(5000, "系统内部异常"),
    NO_AUTH_ERROR(4000,"无权限"),
    PARAMS_ERROR(4001,"参数错误"),
    USER_NOT_LOGIN(40002,"用户未登录"),
    INTERFACE_ADD_ERROR(40000,"接口添加异常"),
    REQUEST_HEARD_ERROR(50002,"请求头不完整");
    private final int code;
    private final String description;

    ResultCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
