package com.compare.favorite.mq.producer;

import com.compare.mq.config.RocketMQConfig;
import com.compare.mq.producer.BaseMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteEventProducer {

    private final BaseMessageProducer baseMessageProducer;

    public void sendFavoriteEvent(Object favorite) {
        baseMessageProducer.send("favoriteProducer-out-0", RocketMQConfig.FAVORITE_TOPIC, favorite);
    }
}
