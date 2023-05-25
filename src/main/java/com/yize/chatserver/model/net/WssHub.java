package com.yize.chatserver.model.net;


import com.yize.chatserver.config.RegisterServer;
import com.yize.chatserver.constants.RedisConstants;
import com.yize.chatserver.utils.PacketUtils;
import com.yize.chatserver.utils.RedisUtilsService;
import com.yize.chatserver.utils.SpringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 websocket 连接池
 */
@Service
public class WssHub {
    private  final Logger logger = LogManager.getLogger(WssHub.class);
    @Value("${chatServer.max-connected}")
    private String maxConnected;
    @Value("${chatServer.server-id}")
    private String serverId;

    /**websocket 记录*/
    private Map<String, Session> websocket;
    private final ReadWriteLock lock;

    public WssHub(){
        lock = new ReentrantReadWriteLock();
    }

    /**插入记录*/
    public void put(String userUid,Session session, String token){
        boolean join = false;
        try {
            boolean tryLock = lock.writeLock().tryLock();
            if(!tryLock) {
                do {
                    tryLock = lock.writeLock().tryLock();
                } while (! tryLock);
            }
            if(websocket == null){
                websocket = new HashMap<>(Integer.parseInt(maxConnected));
            }
            websocket.put(userUid, session);
            join = SessionUtils.put(token, serverId, userUid);
            try {
                if (join) {
                    //发送链接成功
                    RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.ONLINE);
                    String iv = redisUtils.getString(RedisConstants.keys.token + userUid);
                    PacketUtils.sendIv(session, iv);
                    /*更新服务器状态*/
                    RegisterServer bean = SpringUtils.getBean(RegisterServer.class);
                    bean.update();
                }else{
                    //发送链接失败
                    PacketUtils.sendIv(session, "");
                }
            }catch (Exception e){
                logger.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
            }
        }finally {
            lock.writeLock().unlock();
        }

    }

    public Session get(String userUid){
        return websocket.get(userUid);
    }

    /**删除记录*/
    public void rm(String userUid){
        try{
            while (lock.writeLock().tryLock()) {
                Session session = websocket.get(userUid);
                if(session.isOpen()){
                    session.close();
                }
                websocket.remove(userUid);
                SessionUtils.rm(userUid);
            }
        } catch (IOException ex) {
            //throw new RuntimeException(e);
            logger.error(ex.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }
}
