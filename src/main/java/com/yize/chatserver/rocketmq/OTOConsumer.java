package com.yize.chatserver.rocketmq;

import com.alibaba.fastjson2.JSONObject;
import com.yize.chatserver.constants.Topic;
import com.yize.chatserver.controller.WebSocketController;
import com.yize.chatserver.model.net.WssHub;
import com.yize.chatserver.utils.PacketUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 监听信息处理
 * 专注处理信息转发给用户的
 */
@Service
@RocketMQMessageListener(consumerGroup = "default", topic = Topic.OTO)
public class OTOConsumer implements RocketMQListener<String> {

    @Resource
    private WssHub wssHub;
    private  final Logger logger = LogManager.getLogger(WebSocketController.class);
    private final List<String> mqMessage;
    private final ReadWriteLock lock;

    private final ThreadPoolExecutor threadPoolExecutor;

    public OTOConsumer(){
        mqMessage = new LinkedList<>();
        lock = new ReentrantReadWriteLock();
        threadPoolExecutor = new ThreadPoolExecutor(2,4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000));
    }

    @Override
    public void onMessage(String message) {
        boolean r;
        try {
            do {
                r = lock.writeLock().tryLock();
            } while (! r);
            mqMessage.add(message);
            threadPoolExecutor.submit(this::handle);
        }catch (Exception e){
            logger.error(e.getStackTrace());
        }finally {
            lock.writeLock().unlock();
        }
    }

    public void handle(){
        do{
            try{
                if(lock.readLock().tryLock()){
                    Iterator<String> iterator = mqMessage.iterator();
                    while (iterator.hasNext()){
                        String next = iterator.next();
                        JSONObject object = JSONObject.parseObject(next);
                        String targetUid = object.getString("targetUid");
                        Session session = wssHub.get(targetUid);
                        if(session != null){
                            //转发了一条申请加为好友的信息
                            PacketUtils.send(session, object);
                        }
                        iterator.remove();
                    }
                    break;
                }
            }catch (Exception e){
                logger.error(e.getStackTrace());
            }finally {
                lock.readLock().unlock();
            }
        }while (!lock.readLock().tryLock());
    }
}
