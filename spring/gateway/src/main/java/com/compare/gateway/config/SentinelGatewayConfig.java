package com.compare.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.compare.core.result.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SentinelGatewayConfig {

    @PostConstruct
    public void init() {
        initGatewayRules();
        initBlockHandler();
    }

    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("user-service")
                .setCount(100)
                .setIntervalSec(1));
        rules.add(new GatewayFlowRule("shop-service")
                .setCount(100)
                .setIntervalSec(1));
        rules.add(new GatewayFlowRule("product-service")
                .setCount(200)
                .setIntervalSec(1));
        rules.add(new GatewayFlowRule("favorite-service")
                .setCount(100)
                .setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }

    private void initBlockHandler() {
        GatewayCallbackManager.setBlockHandler((ServerWebExchange exchange, Throwable t) -> {
            R<Void> result = R.failed(429, "请求过于频繁，请稍后再试");
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(result));
        });
    }
}
