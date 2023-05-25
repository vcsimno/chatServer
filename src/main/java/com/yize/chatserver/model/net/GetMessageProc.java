package com.yize.chatserver.model.net;

import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 信息池处理
 */
@Service
public class GetMessageProc {
    private  final Logger logger = LogManager.getLogger(GetMessageProc.class);
    @Resource
    private MessageHub messageHub;

    private final ThreadPoolExecutor threadPoolExecutor;

    public GetMessageProc() {
        threadPoolExecutor = new ThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), new ThreadPoolExecutor.CallerRunsPolicy());

    }

    @Bean
    public void process(){
        threadPoolExecutor.submit(this::handle);
    }

    public void put(JSONObject message, Session session){
        messageHub.put(message, session);
    }

    public void handle(){
        long threadId = Thread.currentThread().getId();
        System.out.println("线程开始工作 threadId = " + threadId);
        try {
            for (;;) {
                try {
                    JSONObject message = messageHub.getMessage();
                    System.out.println("message = " + message);
                    if(message.isEmpty()){
                        break;
                    }
                    //TODO
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            System.out.println("线程结束工作 threadId = " + threadId);
        }
    }


}
