package com.wy.yyApiBackend.model.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-19  01:52
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class InterfaceInvokeDTO implements Serializable {
    private static final long serialVersionUID = -3533559247304394102L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求参数
     */
    private String requestParams;
}
