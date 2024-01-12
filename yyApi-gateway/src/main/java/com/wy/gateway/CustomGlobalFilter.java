package com.wy.gateway;

import com.wy.common.exception.BusinessException;
import com.wy.common.model.InterfaceInfo;
import com.wy.common.model.User;
import com.wy.common.result.ResultCode;
import com.wy.common.service.InnerInterfaceInfoService;
import com.wy.common.service.InnerUserInterface;
import com.wy.common.service.InnerUserService;
import com.wy.common.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @Author: wy
 * @CreateTime: 2023-12-16  09:45
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
@Order(value = -1)
public class CustomGlobalFilter implements GlobalFilter {
    private final List<String> IP_WHITE_LIST = List.of("127.0.0.1");
   private static final String INTERFACE_HOST = "http://localhost:8123";
   //private static final String INTERFACE_HOST = "https://api.seniverse.com";

    @DubboReference
    private InnerUserInterface userInterface;
    @DubboReference
    private InnerUserService userService;
    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 1.请求日志
        String id = request.getId(); // 请求唯一标识
        String path = request.getURI().getPath();
        String urlFrom =INTERFACE_HOST+request.getURI().getPath(); //请求路径
        String requestMethod = Objects.requireNonNull(request.getMethod()).name();// 请求方法
        // 请求来源地址
        String remoteAddress = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求唯一标识{}",id);
        log.info("请求路径{}",urlFrom);
        log.info("请求路径{}",path);
        log.info("请求方法{}",requestMethod);
        log.info("请求参数{}",request.getQueryParams());
        log.info("请求来源地址{}",sourceAddress);
        log.info("请求远程地址{}",remoteAddress);
        // 2.访问控制。黑白名单
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3.用户鉴权 判断 ak sk
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        String method = headers.getFirst("method");
        String url = headers.getFirst("url");
        if (StringUtils.isAnyBlank(nonce,timestamp,sign,method)) {
            throw new BusinessException(ResultCode.REQUEST_HEARD_ERROR);
        }
        // 通过ak 获取用户
        User invokeUserBya = null;
        try {
            invokeUserBya = userService.getInvokeUserBya(accessKey);
        } catch (Exception e) {
            log.error("getInvoke user error",e);
        }
        String secretKey = Objects.requireNonNull(invokeUserBya).getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
        if (invokeUserBya == null) {
            return handleNoAuth(response);
        }
        // 随机数
        if (Long.parseLong(Objects.requireNonNull(nonce)) > 10000L) {
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(Objects.requireNonNull(timestamp))) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        // 请求的接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = interfaceInfoService.getInterfaceInfo(url, method);
        } catch (Exception e) {
            log.error("interfaceInfo error",e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUserBya.getId());
        //return handleResponse(exchange,chain,2,1);
    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 处理响应
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            userInterface.invokeInterface(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
}
