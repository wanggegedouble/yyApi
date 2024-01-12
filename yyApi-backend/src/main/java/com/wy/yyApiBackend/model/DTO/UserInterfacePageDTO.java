package com.wy.yyApiBackend.model.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-19  00:41
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfacePageDTO extends PageParam implements Serializable {
    private static final long serialVersionUID = -2273649715285568813L;

    /**
     * 主键
     */
    private Long id;

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

    /**
     * 0-正常，1-禁用
     */
    private Integer status;
}
