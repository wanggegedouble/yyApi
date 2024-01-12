package com.wy.yyApiBackend.service.Impl.inner;

import com.wy.common.service.InnerUserInterface;
import com.wy.yyApiBackend.service.UserInterfaceService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: wy
 * @CreateTime: 2023-12-19  02:34
 * @Description: TODO
 * @Version: 1.0
 */
@DubboService
public class innerUserInterfaceImpl implements InnerUserInterface {

    @Resource
    private UserInterfaceService interfaceService;
    @Override
    public void invokeInterface(long interfaceId, long userId) {
        interfaceService.invokeCount(interfaceId,userId);
    }
}
