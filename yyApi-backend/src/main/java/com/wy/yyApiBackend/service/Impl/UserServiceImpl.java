package com.wy.yyApiBackend.service.Impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.client.yyClinet.YyApiClient;
import com.wy.common.exception.BusinessException;
import com.wy.common.model.User;
import com.wy.common.result.ResultCode;
import com.wy.yyApiBackend.mapper.UserMapper;
import com.wy.yyApiBackend.model.DTO.UserRegister;
import com.wy.yyApiBackend.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import javax.servlet.http.HttpServletRequest;

import static com.wy.yyApiBackend.constant.UserConstant.ADMIN_ROLE;
import static com.wy.yyApiBackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author: wy
 * @CreateTime: 2023-12-16  11:47
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private static final  String SALT = "@!^!@+++@!^!@";

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User loginUser = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(loginUser)) {
            throw new BusinessException(ResultCode.USER_NOT_LOGIN);
        }
        return this.baseMapper.selectById(loginUser.getId());
    }

    @Override
    public void register(UserRegister userRegister) {
        String userAccount = userRegister.getUserAccount();
        String userPassword = userRegister.getUserPassword();
        String checkPassword = userRegister.getCheckPassword();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ResultCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
        }
    }

    @Override
    public void userLogin(String username, String password,HttpServletRequest request) {
        if (username.length() < 4 || password.length() < 8) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户名或密码过短");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,username)
                .eq(User::getUserPassword,encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户名或密码错误");
        }
        User safeUser = this.getSafeUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);
    }

    public User getSafeUser(User user) {
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUserAvatar("");
        safeUser.setGender(user.getGender());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setUserPassword("");
        safeUser.setAccessKey("");
        safeUser.setSecretKey("");
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        return safeUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User loginuser = this.getUserFromSession(request);
        return loginuser != null && ADMIN_ROLE.equals(loginuser.getUserRole());
    }

    @Override
    public YyApiClient getYyApiclient(HttpServletRequest request) {
        User loginuser = this.getUserFromSession(request);
        String accessKey = loginuser.getAccessKey();
        Long id = loginuser.getId();
        User user = this.baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getAccessKey, accessKey).eq(User::getId, id));
        if (user == null) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        String secretKey  = user.getSecretKey();
        return new YyApiClient(accessKey,secretKey);
    }
    public User getUserFromSession(HttpServletRequest request) {
        User loginuser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginuser == null) {
            throw new BusinessException(ResultCode.USER_NOT_LOGIN);
        }
        return loginuser;
    }

}
