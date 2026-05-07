package com.compare.shop.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class ShopEventConsumer {

    @Bean
    public Consumer<Object> shopConsumer() {
        return message -> {
            log.info("Received shop event: {}", message);
        };
    }
}
