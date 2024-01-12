package com.wy.yyApiBackend.service.Impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wy.common.model.InterfaceInfo;
import com.wy.common.service.InnerInterfaceInfoService;
import com.wy.yyApiBackend.mapper.InterfaceInfoMapper;
import com.wy.yyApiBackend.service.InterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: wy
 * @CreateTime: 2023-12-19  03:28
 * @Description: TODO
 * @Version: 1.0
 */
@DubboService
public class InnerInterfaceInfoImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getMethod,method);
        queryWrapper.eq(InterfaceInfo::getUrl,path);
        return this.interfaceInfoMapper.selectOne(queryWrapper);
    }
}
