package com.wy.yyApiBackend.model.DTO;

import lombok.Data;


import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-16  12:54
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UserLoginDTO implements Serializable {
    
    private static final long serialVersionUID = -9143120173738765918L;

    private String username;
    private String password;
}
