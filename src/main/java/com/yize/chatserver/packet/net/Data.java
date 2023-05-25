package com.yize.chatserver.packet.net;

import com.alibaba.fastjson2.JSONObject;

public class Data {

    public final JSONObject pack;

    public Data(){
        pack = new JSONObject();
    }

    public Data header(String value){
        pack.put("actionCode", value);
        return this;
    }

    public Data add(String key, Object value){
        pack.put(key,value);
        return this;
    }

    public JSONObject body(){
        return pack;
    }

    public static Data build(){
       return new Data();
    }
}
