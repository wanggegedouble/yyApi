package com.wy.common.service;

public interface InnerUserInterface {
    /**
     * @Description: 调用统计
     * @param: interfaceId 接口id
     * @param: userId 用户id
     */
    void invokeInterface(long interfaceId,long userId);
}
