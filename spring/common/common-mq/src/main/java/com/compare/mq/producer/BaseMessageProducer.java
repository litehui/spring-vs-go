package com.compare.mq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseMessageProducer {

    private final StreamBridge streamBridge;

    public <T> boolean send(String bindingName, String topic, T payload) {
        Message<T> message = MessageBuilder.withPayload(payload)
                .setHeader("topic", topic)
                .build();
        boolean result = streamBridge.send(bindingName, message);
        log.info("Send message to [{}] topic [{}]: success={}", bindingName, topic, result);
        return result;
    }

    public <T> boolean sendDelayed(String bindingName, String topic, T payload, int delayLevel) {
        Message<T> message = MessageBuilder.withPayload(payload)
                .setHeader("topic", topic)
                .setHeader("delayLevel", delayLevel)
                .build();
        boolean result = streamBridge.send(bindingName, message);
        log.info("Send delayed message to [{}] topic [{}]: delayLevel={}, success={}", bindingName, topic, delayLevel, result);
        return result;
    }
}
