package com.yize.chatserver.model.net;

import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class MessageHub {
    private  final Logger logger = LogManager.getLogger(MessageHub.class);
    private final ReadWriteLock lock;
    /**信息池*/
    private final List<JSONObject> messageHub;

    public MessageHub() {

        messageHub = new CopyOnWriteArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    /**插入信息*/
    public void put(JSONObject msg, Session session){
        try {
            boolean tryLock = lock.writeLock().tryLock();
            if(!tryLock) {
                do {
                    tryLock = lock.writeLock().tryLock();
                } while (! tryLock);
            }
            messageHub.add(msg);
        }catch (Exception ignore){
            try{
                session.getBasicRemote().sendText("发送信息失败");
            }catch (IOException e){
                logger.error(e.getMessage());
            }
        }finally {
            lock.writeLock().unlock();
        }
    }

    public JSONObject getMessage(){
        JSONObject message = new JSONObject();
        try {
            if (lock.readLock().tryLock()) {
                if (! messageHub.isEmpty()) {
                    message = messageHub.get(0);
                    //处理信息
                    messageHub.remove(message);
                }
            }
        }finally {
            lock.readLock().unlock();
        }
        return message;
    }
}
