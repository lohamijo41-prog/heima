package com.heima.app.gateway.filter;

import com.heima.common.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.判断是否是登录
        if (request.getURI().getPath().contains("/login")) {
            //放行
            return chain.filter(exchange);
        }
        //3.获取Token
        String token = request.getHeaders().getFirst("token");
        //4.判断Token是否存在
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //5.判断Token是否有效
        try {
            Claims claimsBody = JWTUtil.getClaimsBody(token);
            //是否是过期
            int result = JWTUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //6.放行
        return chain.filter(exchange);
    }
    /**
     * 优先级设置  值越小  优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
