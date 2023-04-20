package com.yize.chatserver.packet.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 数据包签名栏
 */
@Data
public class SignVo {
   private long timeStamp;
   private String iv;
   private String randomString;

   @Override
   public String toString(){
       JSONObject object = new JSONObject();
       object.put("timeStamp", timeStamp);
       object.put("iv", iv);
       object.put("randomString", randomString);
       return object.toJSONString();
   }
}
