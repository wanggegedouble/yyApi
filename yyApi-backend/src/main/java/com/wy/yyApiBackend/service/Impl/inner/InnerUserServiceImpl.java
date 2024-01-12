package com.wy.yyApiBackend.service.Impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wy.common.exception.BusinessException;
import com.wy.common.model.User;
import com.wy.common.result.ResultCode;
import com.wy.common.service.InnerUserService;
import com.wy.yyApiBackend.mapper.UserMapper;
import com.wy.yyApiBackend.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: wy
 * @CreateTime: 2023-12-19  03:07
 * @Description: TODO
 * @Version: 1.0
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUserBya(String accessKey) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccessKey,accessKey);
        return this.userMapper.selectOne(queryWrapper);
    }
}
