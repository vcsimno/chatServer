package com.yize.chatserver.packet.net;

import com.alibaba.fastjson.JSONObject;
import com.yize.chatserver.exception.YiException;
import com.yize.chatserver.packet.vo.PacketVo;
import com.yize.chatserver.packet.vo.SignVo;
import com.yize.chatserver.utils.AesEncryptUtils;
import com.yize.chatserver.utils.RsaUtils;

/**
 * 打包
 */
public class Packer {

    public static String Pack(JSONObject data, SignVo sign){
        PacketVo vo = new PacketVo();
        vo.setData(data);
        vo.setSign(sign.toString());

        String encrypt = RsaUtils.encrypt(sign.toString());
        vo.setSign(encrypt);

        //AES 加密
        try {
            String aes = AesEncryptUtils.encrypt(vo.toString());
            return aes;
        }catch (Exception e){
            throw new YiException(e.getMessage());
        }

    }
}
