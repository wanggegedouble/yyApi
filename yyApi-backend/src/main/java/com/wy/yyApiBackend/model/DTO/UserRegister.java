package com.wy.yyApiBackend.model.DTO;

import lombok.Data;


import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-16  12:20
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UserRegister implements Serializable {
    
    private static final long serialVersionUID = 7503023280084311334L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 星球编号
     */
    private String planetCode;
}
