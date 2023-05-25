package com.yize.chatserver.config;

import com.alibaba.fastjson.JSONObject;
import com.yize.chatserver.constants.RedisConstants;
import com.yize.chatserver.utils.RedisUtilsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RegisterServer {
    @Value("${chatServer.server-name}")
    private String serverName;
    @Value("${chatServer.server-id}")
    private String serverId;
    @Value("${chatServer.max-connected}")
    private String maxConnected;
    @Value("${chatServer.server-ip}")
    private String serverIp;
    @Value("${chatServer.server-port}")
    private String serverPort;
    @Value("${chatServer.server-path}")
    private String serverPath;

    @Bean
    public void register(){
        RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.ONLINE);
        JSONObject object = new JSONObject();
        object.put("ip", serverIp);
        object.put("port", serverPort);
        object.put("path", serverPath);
        object.put("online", 0);
        object.put("maxConnected", maxConnected);
        object.put("name", serverName);
        object.put("status", 0);
        object.put("ping", 0);
        redisUtils.set(RedisConstants.keys.chatServer + serverId, object.toJSONString());
    }

    /**
     * 更新服务器在线状态
     */
    public void update(){
        RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.ONLINE);
        String cache = redisUtils.get(RedisConstants.keys.chatServer + serverId);

        JSONObject object = JSONObject.parseObject(cache);
        String online = object.getString("online");
        object.put("online", Integer.parseInt(online) + 1);
        redisUtils.set(RedisConstants.keys.chatServer + serverId, object.toJSONString());
    }
}
