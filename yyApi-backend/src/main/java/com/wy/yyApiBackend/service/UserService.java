package com.wy.yyApiBackend.service;

import com.wy.client.yyClinet.YyApiClient;
import com.wy.common.model.User;
import com.wy.yyApiBackend.model.DTO.UserRegister;

import javax.servlet.http.HttpServletRequest;


public interface UserService {
    User getLoginUser(HttpServletRequest request);

    void register(UserRegister userRegister);

    void userLogin(String username, String password,HttpServletRequest request);

    /**
     *  获取脱敏的用户数据
     * @param user 原始用户数据
     * @return 脱敏用户
     */
    User getSafeUser(User user);

    /**
     * 是否是管理员
     * @param request HttpServletRequest
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     *  生成yyAPIClient
     */
    YyApiClient getYyApiclient(HttpServletRequest request);

    /**
     *   从session 中获取User
     * @param request HttpServletRequest
     */
    User getUserFromSession(HttpServletRequest request);
}
