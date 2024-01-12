package com.wy.client.yyClinet;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.wy.client.utils.SignUtils.genSign;
/**
 * @Author: wy
 * @CreateTime: 2023-12-16  03:50
 * @Description: TODO
 * @Version: 1.0
 */

@Slf4j
public class YyApiClient {
    private final String accessKey;
    private final String secretKey;

    public YyApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private Map<String, String> getHeaderMap(String params,String url,String method) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", params);
        hashMap.put("url",url);
        hashMap.put("method",method);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(params, secretKey));
        return hashMap;
    }

    public String invokeInterface(String params, String url, String method) {
        String GATEWAY_HOST = "http://127.0.0.1:8091";
        HttpResponse httpResponse = null;
        switch (method) {
            case "POST":
                httpResponse = HttpRequest.post(GATEWAY_HOST + url)
                        .addHeaders(getHeaderMap(params,url,method))
                        .body(params)
                        .execute();
                break;
            case "GET":
                HashMap<String, String> paramHashMap = getParamHashMap(params);
                log.info(GATEWAY_HOST + url);
                httpResponse = HttpRequest.get(GATEWAY_HOST + url)
                        .addHeaders(getHeaderMap(params, url,method))
                        .formStr(paramHashMap)
                        .execute();
                break;
        }
        return JSONUtil.formatJsonStr(Objects.requireNonNull(httpResponse).body());
    }

    private HashMap<String,String> getParamHashMap(String requestParam) {
//        log.info(requestParam);
//        HashMap<String, Object> stringObjectHashMap = JSON.parseObject(requestParam, new TypeReference<HashMap<String, Object>>() {
//        });
//        log.info(String.valueOf(stringObjectHashMap));
        HashMap<String,String> map = new HashMap<>();
        map.put("key","SXFprD7aKpzusII1h");
        map.put("location","北京");
        return map;
    }


}
