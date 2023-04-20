/*
 * Copyright (c) 2022. yize.link
 * editor: yize
 * date:  2022/11/8
 *
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */
package com.yize.chatserver.constants;

/**
 * redis 用 全局变量
 */
public class RedisConstants {

    /**redis key*/
    public static class keys{
        public final static String token = "auth:";
        /**系统配置*/
        public static final String CONF = "conf:";

        public static final String chatServer = "chatServer:";

        public static final String VERIFY_CODE_LOCK = "vc:lock:";
        public static final String VERIFY_CODE = "vc:";
    }



    /**用户在线表*/
    public static final String USERS = "USERS";
    /**在线表*/
    public static final String ONLINE = "ONLINE";
}
