package com.compare.shop.mq.producer;

import com.compare.mq.config.RocketMQConfig;
import com.compare.mq.producer.BaseMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopEventProducer {

    private final BaseMessageProducer baseMessageProducer;

    public void sendShopCreatedEvent(Object shop) {
        baseMessageProducer.send("shopProducer-out-0", RocketMQConfig.SHOP_TOPIC, shop);
    }

    public void sendShopUpdatedEvent(Object shop) {
        baseMessageProducer.send("shopProducer-out-0", RocketMQConfig.SHOP_TOPIC, shop);
    }
}
