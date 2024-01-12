package com.wy.yyApiBackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: wy
 * @CreateTime: 2023-12-16  02:24
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = "com.wy.**")
@MapperScan("com.wy.yyApiBackend.mapper")
@EnableDubbo
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class,args);
    }
}
