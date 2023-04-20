package com.yize.chatserver.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesEncryptUtils {
    //可配置到Constant中，并读取配置文件注入,16位,自己定义
    private static final String KEY = "bwXbe0TYMDmK7E8f";
    //偏移量,AES 为16bytes. DES 为8bytes
    private static final String IV = "MJTRC8NacfOC2rrE";


    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String AES_TYPE = "AES/CBC/PKCS5Padding";

    /**
     * 加密
     * @param content 加密的字符串
     * @return 加密结果
     * @throws Exception 抛出异常
     */
    public static String encrypt(String content) throws Exception {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(AES_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
        //初始化，此方法可以采用三种方式，按加密算法要求来添加。
        // （1）无第三个参数
        // （2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
        // （3）采用此代码中的IVParameterSpec(ECB不能使用偏移量)
        //加密时使用:ENCRYPT_MODE;  解密时使用:DECRYPT_MODE;
        byte[] b = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        // 采用base64算法进行转码,避免出现中文乱码
        String result = Base64.getEncoder().encodeToString(b);
        return result.replaceAll("/", "_");

    }

    /**
     * 解密
     * @param encryptStr 解密的字符串
     * @return 解密数据
     * @throws Exception 抛出异常
     */
    public static String decrypt(String encryptStr) throws Exception {
        encryptStr = encryptStr.replaceAll("_", "/");

        byte[] decodeByte = Base64.getDecoder().decode(encryptStr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(AES_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] decryptBytes = cipher.doFinal(decodeByte);
        return new String(decryptBytes);
    }
}
