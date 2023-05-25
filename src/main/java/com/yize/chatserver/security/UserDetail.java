/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.security;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * spring security user
 */
@Data
@AllArgsConstructor
public class UserDetail {

    private String password;
    private String nickName;
    private String uid;
    private String userGroup;
    private String userTx;
    private String mobile;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
}
