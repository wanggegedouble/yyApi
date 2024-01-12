package com.wy.yyApiBackend.controller;

import com.wy.common.exception.BusinessException;
import com.wy.common.model.User;
import com.wy.common.result.RestResult;
import com.wy.common.result.ResultCode;
import com.wy.yyApiBackend.model.DTO.UserLoginDTO;
import com.wy.yyApiBackend.model.DTO.UserRegister;
import com.wy.yyApiBackend.service.UserService;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.wy.yyApiBackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author: wy
 * @CreateTime: 2023-12-16  12:15
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public RestResult<String> register(@RequestBody UserRegister userRegister) {
        if (userRegister == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.userService.register(userRegister);
        return RestResult.of("注册成功");
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public RestResult<String> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        if (StringUtils.isAllBlank(username, password)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.userService.userLogin(username,password,request);
        return RestResult.of("登录成功");
    }

    @GetMapping("/get")
    @ApiOperation(value = "当前登录用户")
    public RestResult<User> getLoginUser(HttpServletRequest request) {
        User user= (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessException(ResultCode.USER_NOT_LOGIN);
        }
        User safeUser = this.userService.getSafeUser(user);
        return RestResult.of("当前登录用户",safeUser);
    }

}
