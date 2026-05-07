package com.compare.favorite.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class FavoriteEventConsumer {

    @Bean
    public Consumer<Object> favoriteConsumer() {
        return message -> {
            log.info("Received favorite event: {}", message);
        };
    }
}
