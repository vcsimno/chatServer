package com.yize.chatserver.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.yize.chatserver.constants.ActionConstants;
import com.yize.chatserver.packet.net.Data;
import com.yize.chatserver.packet.net.Packer;
import com.yize.chatserver.packet.vo.SignVo;
import javax.websocket.Session;


/**
 * 发送工具
 */
public class PacketUtils {

    public static void send(Session session, JSONObject data){
        SignVo signVo = new SignVo();
        signVo.setIv("server");
        signVo.setRandomString(RandomUtil.randomString(6));
        signVo.setTimeStamp(System.currentTimeMillis());
        String pack = Packer.Pack(data, signVo);
        try{
            if(session.isOpen()) {
                session.getBasicRemote().sendText(pack);
            }
        }catch (Exception ignore){
        }
    }

    public static void sendIv(Session session, String iv){
       // System.out.println("session = " + session);
        JSONObject body = Data.build().header(ActionConstants.USER_LOGIN).add("iv", iv).body();
        send(session, body);
    }
}
