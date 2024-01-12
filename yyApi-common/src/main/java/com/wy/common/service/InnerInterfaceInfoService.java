package com.wy.common.service;

import com.wy.common.model.InterfaceInfo;

public interface InnerInterfaceInfoService {
    /**
     * @Description: 通过 url与method查询接口信息
     * @param: url 接口路径
     * @param: method 请求方法
     * @return: 数据库中存在的记录
     */
    InterfaceInfo getInterfaceInfo(String url,String method);
}
