package com.wy.client;

import com.wy.client.yyClinet.YyApiClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wy
 * @CreateTime: 2023-12-16  03:46
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties("yyapi.client")
@ComponentScan
public class YyApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public YyApiClient yyApiClient() {
        return new YyApiClient(accessKey,secretKey);
    }
}

