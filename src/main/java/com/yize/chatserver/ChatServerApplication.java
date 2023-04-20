package com.yize.chatserver;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.yize.chatserver.constants.RedisConstants;
import com.yize.chatserver.packet.net.Packer;
import com.yize.chatserver.packet.vo.SignVo;
import com.yize.chatserver.utils.RedisUtilsService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan( basePackages = "com.yize.chatserver.mapper")
public class ChatServerApplication {

    public static void main(String[] args) {
        String[] param = new String[args.length+1];
        int index = 0;
        for(int i = 0; i < args.length; ++i){
            param[i] = args[i];
            ++index;
        }
        param[index] = "--mpw.key="+"04dc7e59eeaa8ab6";
        SpringApplication.run(ChatServerApplication.class, param);

        RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.ONLINE);
        //Set<String> keys = redisUtils.keys(RedisConstants.keys.chatServer + "*");
        JSONObject object = new JSONObject();
        object.put("ip", "127.0.0.1");
        object.put("port", "9556");
        object.put("path", "cs_server");
        object.put("online", 0);
        object.put("name", "lisa");
        object.put("status", 0);
        object.put("ping", 0);
        redisUtils.set(RedisConstants.keys.chatServer + "lisa", object.toJSONString());


        /*
        String encrypt = RsaUtils.encrypt("加密");
        System.out.println("encrypt = " + encrypt);
        */
        JSONObject data = new JSONObject();
        data.put("uid", 123456L);
        data.put("userName", "administrator");
        SignVo signVo = new SignVo();
        signVo.setIv("IV123456");
        signVo.setTimeStamp(System.currentTimeMillis());
        signVo.setRandomString(RandomUtil.randomString(6));

        String pack = Packer.Pack(data, signVo);
        System.out.println("pack = " + pack);
    }

}
