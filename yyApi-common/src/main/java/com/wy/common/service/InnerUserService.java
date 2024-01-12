package com.wy.common.service;

import com.wy.common.model.User;

public interface InnerUserService {
    /**
     * @Description: 通过 accessKey 查询是否存在该用户
     */
    User getInvokeUserBya(String accessKey);
}
