package com.compare.user.mq.producer;

import com.compare.mq.config.RocketMQConfig;
import com.compare.mq.producer.BaseMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final BaseMessageProducer baseMessageProducer;

    public void sendUserCreatedEvent(Object user) {
        baseMessageProducer.send("userProducer-out-0", RocketMQConfig.USER_TOPIC, user);
    }

    public void sendUserUpdatedEvent(Object user) {
        baseMessageProducer.send("userProducer-out-0", RocketMQConfig.USER_TOPIC, user);
    }
}
