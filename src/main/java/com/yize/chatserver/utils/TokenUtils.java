/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.utils;
import cn.hutool.jwt.JWTUtil;
import com.yize.chatserver.security.UserDetail;
/**
 * JWT token 工具类
 */
public class TokenUtils {

    /**读取token*/
    public static String getToken(String token){
        return token.split(" ")[1];
    }
    public static UserDetail getUserDetails(String token){
        return JWTUtil.parseToken(token).getPayload().getClaimsJson().getJSONObject("userDetail").toBean(UserDetail.class);
    }
}
