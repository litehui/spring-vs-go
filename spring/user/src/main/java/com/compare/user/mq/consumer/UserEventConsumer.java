package com.compare.user.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class UserEventConsumer {

    @Bean
    public Consumer<Object> userConsumer() {
        return message -> {
            log.info("Received user event: {}", message);
        };
    }
}
