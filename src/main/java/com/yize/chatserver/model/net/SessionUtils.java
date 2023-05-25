package com.yize.chatserver.model.net;

import com.yize.chatserver.constants.RedisConstants;
import com.yize.chatserver.utils.RedisUtilsService;
import org.springframework.stereotype.Service;

@Service
public class SessionUtils {

    /**根据token换来链接的IV*/
    public static boolean put(String token, String serverId, String userUid){
        RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.USERS);
        if(!redisUtils.hasKey(RedisConstants.keys.token + token)){
            /*token 无效。*/
            return false;
        }
        String iv = redisUtils.getString(RedisConstants.keys.token + token);

        if(!redisUtils.hasKey(RedisConstants.keys.token + iv)){
            /*token 无效。*/
            return false;
        }

        RedisUtilsService.RedisUtils online = RedisUtilsService.instance(RedisConstants.ONLINE);
        /*把iv写入online表，并且标记，是来自于哪个服务器的*/
        online.set(RedisConstants.keys.token + iv, serverId, -1);
        online.set(RedisConstants.keys.token + userUid, iv, -1);
        return true;
    }

    /**删除登录信息*/
    public static void rm(String userUid){
        RedisUtilsService.RedisUtils online = RedisUtilsService.instance(RedisConstants.ONLINE);
        if(online.hasKey(RedisConstants.keys.token + userUid)){
            String iv = online.getString(RedisConstants.keys.token + userUid);
            online.del(RedisConstants.keys.token + userUid);
            online.del(RedisConstants.keys.token + iv);
        }
    }
}
