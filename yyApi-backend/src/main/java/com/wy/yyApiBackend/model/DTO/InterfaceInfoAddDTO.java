package com.wy.yyApiBackend.model.DTO;

import lombok.Data;


import java.io.Serializable;

/**
 * 接口创建请求

 */
@Data
public class InterfaceInfoAddDTO implements Serializable {

    
    private static final long serialVersionUID = -4195167453097929851L;
    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 请求类型
     */
    private String method;

}