package com.wy.common.exception;


import com.wy.common.result.ResultCode;
import lombok.Getter;
import lombok.Setter;


/**
 * @Author: wy
 * @CreateTime: 2023-10-22  17:30
 * @Description: TODO
 * @Version: 1.0
 */

@Getter
@Setter
public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 2506322518193617739L;
    private final int errorCode;

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ResultCode resultCode,String message) {
        super(message);
        this.errorCode = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getDescription());
        this.errorCode = resultCode.getCode();
    }

}
