/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.domain;

import com.yize.chatserver.security.UserDetail;
import lombok.Data;

import java.io.Serializable;

/**
 * JWT 有效载荷
 * 账号主体信息
 */
@Data
public class TokenPayLoad implements Serializable {
    /**签发时间*/
    private String issuedAt;
    /**过期时间*/
    private String expiresAt;
    /**生效时间*/
    private String notBefore;
    /**用户细节*/
    private UserDetail userDetail;
    /**IV*/
    private String iv;
}
