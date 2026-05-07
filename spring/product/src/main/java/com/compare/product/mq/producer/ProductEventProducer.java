package com.compare.product.mq.producer;

import com.compare.mq.config.RocketMQConfig;
import com.compare.mq.producer.BaseMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final BaseMessageProducer baseMessageProducer;

    public void sendProductCreatedEvent(Object product) {
        baseMessageProducer.send("productProducer-out-0", RocketMQConfig.PRODUCT_TOPIC, product);
    }

    public void sendProductUpdatedEvent(Object product) {
        baseMessageProducer.send("productProducer-out-0", RocketMQConfig.PRODUCT_TOPIC, product);
    }

    public void sendStockChangedEvent(Object product) {
        baseMessageProducer.send("productProducer-out-0", RocketMQConfig.PRODUCT_TOPIC, product);
    }
}
