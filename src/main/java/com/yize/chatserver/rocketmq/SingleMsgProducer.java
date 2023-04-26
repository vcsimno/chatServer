package com.yize.chatserver.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.yize.chatserver.constants.Topic;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.yize.chatserver.constants.Topic.OTO;


/**
 * 单信息发送器
 */
@Service
public class SingleMsgProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**发送异步信息*/
    public SendResult send(Topic topic, JSONObject payload){
        return doSend(topic, payload, rocketMQTemplate.getProducer().getNamespace());
    }

    /**发送异步信息*/
    public SendResult send(Topic topic, JSONObject payload, String nameSpace){
        return doSend(topic, payload, nameSpace);
    }

    private SendResult doSend(Topic topic, JSONObject payload, String nameSpace){
        try {
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            producer.setNamespace(nameSpace);
            rocketMQTemplate.setProducer(producer);
            return rocketMQTemplate.syncSend(topic.toString(), payload.toJSONString());
        }catch (Exception e){
            SendResult sendResult = new SendResult();
            sendResult.setSendStatus(SendStatus.FLUSH_SLAVE_TIMEOUT);
            return sendResult;
        }
    }


    public void test(){
        new ThreadPoolExecutor(1, 1, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<>()).execute(()->{
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for(int i=0; i< 100 ; ++i){
                JSONObject r = new JSONObject();
                send(OTO, r);
            }
        });
    }
}
