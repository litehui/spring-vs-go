package com.compare.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[Gateway] {} {} cost={}ms status={}", method, path, costTime,
                    exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
