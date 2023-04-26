package com.yize.chatserver.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.yize.chatserver.packet.net.Packer;
import com.yize.chatserver.packet.vo.PacketVo;
import com.yize.chatserver.packet.vo.SignVo;
import com.yize.chatserver.utils.AesEncryptUtils;
import com.yize.chatserver.utils.RsaUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Service
@ServerEndpoint(value = "/cs_server")
@DependsOn({"springUtils"})
public class WebSocketController {

    public WebSocketController(){

    }

    private  final Logger logger = LogManager.getLogger(WebSocketController.class);

    @OnOpen
    public void onOpen (Session session) {
        System.out.println("链接 session = " + session);
    }


    @OnClose
    public void onClose (Session session) throws IOException {
        if(session.isOpen()) {
            session.close();
        }
    }

    @OnMessage
    public void onMessage (String message, Session session) {
        
        System.out.println("message = " + message);

        try{
            String decrypt = AesEncryptUtils.decrypt(message);
            PacketVo packetVo = JSONObject.parseObject(decrypt, PacketVo.class);

            System.out.println("packetVo = " + packetVo);

            String sign = RsaUtils.decrypt(packetVo.getSign());
            SignVo signVo = JSONObject.parseObject(sign, SignVo.class);

            System.out.println("signVo = " + signVo);

        }catch (Exception e){
            e.printStackTrace();
        }
        


        JSONObject data = new JSONObject();
        data.put("uid", 123456L);
        data.put("userName", "administrator");
        SignVo signVo = new SignVo();
        signVo.setIv("IV123456");
        signVo.setTimeStamp(System.currentTimeMillis());
        signVo.setRandomString(RandomUtil.randomString(6));

        try{
            String pack = Packer.Pack(data, signVo);
            session.getBasicRemote().sendText(pack);
        }catch (IOException e){
            System.out.println("e = " + e.getMessage());
        }


    }

    @OnError
    public void onError (Session session, Throwable error) {
        try {
            onClose(session);
        }catch (Throwable e) {
           logger.error(e.getMessage());
        }
    }
}
