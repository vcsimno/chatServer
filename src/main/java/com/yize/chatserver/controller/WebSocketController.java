package com.yize.chatserver.controller;

import com.alibaba.fastjson2.JSONObject;
import com.yize.chatserver.constants.RedisConstants;
import com.yize.chatserver.model.net.GetMessageProc;
import com.yize.chatserver.model.net.WssHub;
import com.yize.chatserver.packet.vo.PacketVo;
import com.yize.chatserver.packet.vo.SignVo;
import com.yize.chatserver.utils.*;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;

import static com.yize.chatserver.constants.ActionConstants.USER_LOGIN;

@Service
@ServerEndpoint(value = "/cs_server")
@DependsOn({"springUtils"})
@NoArgsConstructor
public class WebSocketController {



    private  final Logger logger = LogManager.getLogger(WebSocketController.class);
    @OnOpen
    public void onOpen (Session session) {

    }

    @OnClose
    public void onClose (Session session) throws IOException {
        if(session.isOpen()) {
            session.close();
        }
    }

    @OnMessage
    public void onMessage (String message, Session session) {
        try{
            try {
                String decrypt = AesEncryptUtils.decrypt(message);
                System.out.println("WebSocketController ---> packet = " + decrypt);
                PacketVo packetVo = JSONObject.parseObject(decrypt, PacketVo.class);
                String sign = RsaUtils.decrypt(packetVo.getSign());
                SignVo signVo = JSONObject.parseObject(sign, SignVo.class);
                long currentTimeMillis = System.currentTimeMillis();
                if (signVo == null) {
                    return;
                }
                long timeStamp = signVo.getTimeStamp();

                if ((currentTimeMillis - timeStamp) > 10000) {
                    //数据包超过十秒无效
                    return;
                }
                GetMessageProc getMessageProc = SpringUtils.getBean(GetMessageProc.class);
                if(signVo.getIv() == null || signVo.getIv().isEmpty()){
                    //触发新链接加入
                    if(packetVo.getData().getString("token") == null){
                        return;
                    }
                    if(packetVo.getData().getString("actionCode") == null){
                        return;
                    }
                    if(!packetVo.getData().getString("actionCode").equals(USER_LOGIN)){
                        return;
                    }
                    String token = TokenUtils.getToken(packetVo.getData().getString("token"));
                    RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.USERS);
                    boolean hasKey = redisUtils.hasKey(RedisConstants.keys.token + token);
                    if(!hasKey){
                        return;
                    }
                    String uid = TokenUtils.getUserDetails(token).getUid();
                    WssHub wssHub = SpringUtils.getBean(WssHub.class);
                    wssHub.put(uid,session,token);
                }else {
                    /*触发信息处理线程*/
                    getMessageProc.put(com.alibaba.fastjson2.JSONObject.parseObject(packetVo.getData().toJSONString()), session);
                    getMessageProc.process();
                }
            }catch (Exception e){
                logger.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    @OnError
    public void onError (Session session, Throwable error) {
        try {
            onClose(session);
        }catch (Throwable e) {
           logger.error(e.getMessage());
        }
        logger.error(error.getMessage());
    }
}
