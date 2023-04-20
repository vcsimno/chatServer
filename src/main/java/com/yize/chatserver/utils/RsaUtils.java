package com.yize.chatserver.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;


/**
 * RSA加密数据
 */
public class RsaUtils {

    private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKCXjblRmYkF364ecRVKjM+47py+N6/niRXe5F5OkOCHn6kb/vl2cdUfgd9FKxQGWQqVdwChyq6wo1Lm4TGhERMiCeolMjaZrS1GsNHmR4udwCOodT56G6wJDL6Ev2Xnrvvs6WQ+BnLFyg3P7F/CDs4rRekYLOWYamG1+0v5tXhPAgMBAAECgYAIZMgNkQxJDpb3PNipajoboDIEAQLuZnyJOYjqKq4729G40VhbN+J/QqRTaJZjidcdA1ygrK+fCULEraZXAN67zWHgE6BbZQyxCIQcng3DKinti+yoHtFFqLJyCU11xYGjhV7U2+UJnPeXKJSU1AHNXzlZg7hfT++kJqYKvE+R8QJBAOI3zYyga+rG+5v3v6VMQRIjvqGKJwFdm68+yYpKGr4WXApxyYJrPIyngGLw6tdH8wpTbQurr5ZDswYjiP1FILMCQQC1u/dqQ+1BVLtYeKvwjz1lwYcTGHwP0SOdCXoMEbrqWanYV0fD5iIbv7NQeWXMR8nc59acqqpLbVUnYiG+yp/1AkBnPfyLj7Nw5Pt0BS/r6s9Pzgx0gS6oCubApjEo8gUed+ntwlyrLvHlZ5TKkxU6x1V+gvCjgOmT9LXBuXoNzPzNAkEAgO2PA0aZZQXVu1gwswMLzcrUJwnxyc7Xcysr+LADaQqfYinZcfMsM392mRrmy1wKq7uWF6NEGSrBhKUrx408mQJACH9kqxrP35v2l+WqmpznLNJqSSbw6KomA5hxzGt3UrrjLrAWvsC7qo/+e7PJq/GFTWyS8UTXL7IXdnlAcbWrhw==";
    public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgl425UZmJBd+uHnEVSozPuO6cvjev54kV3uReTpDgh5+pG/75dnHVH4HfRSsUBlkKlXcAocqusKNS5uExoRETIgnqJTI2ma0tRrDR5keLncAjqHU+ehusCQy+hL9l56777OlkPgZyxcoNz+xfwg7OK0XpGCzlmGphtftL+bV4TwIDAQAB";


    /**
     * 类型
     */
    public static final String ENCRYPT_TYPE = "RSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "PUBLIC_KEY";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    /**
     * 公钥加密
     * @param content 要加密的内容
     */
    public static String encrypt(String content) {
        try{
            RSA rsa = new RSA(privateKey, null);
            return rsa.encryptBase64(content, KeyType.PrivateKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥加密
     * @param content 要加密的内容
     * @param publicKey 公钥
     */
    public static String encrypt(String content, String publicKey) {
        try{
            RSA rsa = new RSA(null,publicKey);
            return rsa.encryptBase64(content, KeyType.PublicKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 私钥解密
     * @param content 要解密的内容
     * @param privateKey 私钥
     */
    public static String decrypt(String content, PrivateKey privateKey) {
        try {
            RSA rsa = new RSA(privateKey , null);
            return rsa.decryptStr(content, KeyType.PrivateKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥解密
     * @param content 要解密的内容
     */
    public static String decrypt(String content) {
        try {
            RSA rsa = new RSA(privateKey, null);
            return rsa.decryptStr(content, KeyType.PrivateKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取公私钥-请获取一次后保存公私钥使用
     * @return generateKeyPair
     */
    public static Map<String,String> generateKeyPair() {
        try {
            KeyPair pair = SecureUtil.generateKeyPair(ENCRYPT_TYPE);
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            // 获取 公钥和私钥 的 编码格式（通过该 编码格式 可以反过来 生成公钥和私钥对象）
            byte[] pubEncBytes = publicKey.getEncoded();
            byte[] priEncBytes = privateKey.getEncoded();

            // 把 公钥和私钥 的 编码格式 转换为 Base64文本 方便保存
            String pubEncBase64 = Base64.encode(pubEncBytes);
            String priEncBase64 = Base64.encode(priEncBytes);

            Map<String, String> map = new HashMap<String, String>(2);
            map.put(PUBLIC_KEY,pubEncBase64);
            map.put(PRIVATE_KEY,priEncBase64);

            return map;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
