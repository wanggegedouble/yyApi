package com.wy.yyApiBackend.model.DTO;

import com.wy.yyApiBackend.constant.CommonConstant;
import lombok.Data;

/**
 * @Author: wy
 * @CreateTime: 2023-12-17  00:01
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class PageParam {
    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
