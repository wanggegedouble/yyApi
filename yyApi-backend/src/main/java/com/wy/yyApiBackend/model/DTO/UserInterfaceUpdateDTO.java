package com.wy.yyApiBackend.model.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-18  23:30
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UserInterfaceUpdateDTO implements Serializable {
    private static final long serialVersionUID = -3527401793486623264L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;
}
