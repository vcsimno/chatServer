package com.yize.chatserver.packet.vo;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * 数据互传VO
 */
@Data
public class PacketVo {
    private String sign;
    private JSONObject data;

    @Override
    public String toString(){
        JSONObject object = new JSONObject();
        object.put("sign", sign);
        object.put("data", data);
        return object.toJSONString();
    }
}
