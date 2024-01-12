package com.wy.yyApiBackend.model.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-18  22:22
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UserInterfaceDTO implements Serializable {
    private static final long serialVersionUID = 1013643464787144141L;


    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;
}
