package com.yize.chatserver.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "OTO")
public class MqConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("------- OrderPaidEventConsumer received:"+message);
    }


}
