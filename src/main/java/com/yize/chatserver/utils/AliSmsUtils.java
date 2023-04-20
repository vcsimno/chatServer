/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.utils;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.yize.chatserver.constants.RedisConstants;
import com.yize.chatserver.exception.YiException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author yizeLink
 */
@Service
@NoArgsConstructor
public class AliSmsUtils {

    @Value("${aliyun.sms.access-key-id}")
    private String key;
    @Value("${aliyun.sms.access-key-secret}")
    private String secret;
    @Value("${aliyun.sms.cool-down}")
    private String coolDown;
    @Value("${aliyun.sms.signName}")
    private String signName;


    /**发送SMS短信*/
    public boolean sendSms(String mobile){
        try {
            RedisUtilsService.RedisUtils redisUtils = RedisUtilsService.instance(RedisConstants.USERS);

            Config config = new Config();
            config.setAccessKeyId(key);
            config.setAccessKeySecret(secret);

            String lockKey = RedisConstants.keys.VERIFY_CODE_LOCK + mobile;
            String key = RedisConstants.keys.VERIFY_CODE + mobile;
            if(!redisUtils.getString(lockKey).isEmpty()){
                throw new YiException("请勿频繁操作获取验证码。");
            }

            String next = RandomUtil.randomNumbers(4);
            JSONObject object = new JSONObject();
            object.put("code", next);
            Client client = new Client(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            sendSmsRequest.setTemplateCode("SMS_154950909"); // 验证码模板
            sendSmsRequest.setSignName(signName);
            sendSmsRequest.setPhoneNumbers(mobile);
            sendSmsRequest.setTemplateParam(object.toJSONString());
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            redisUtils.set(lockKey, mobile, Integer.parseInt(coolDown));
            TimeUnit.MILLISECONDS.sleep(100);
            redisUtils.set(key, next, 60*10);
            return "OK".equals(sendSmsResponse.getBody().getCode());
        }catch (Exception e){
            throw new YiException(e.getMessage());
        }
    }

}
