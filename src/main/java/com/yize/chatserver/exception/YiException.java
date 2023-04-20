/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.exception;

/**
 * 自定义异常类
 */
public class YiException extends RuntimeException {
    public YiException(String e){
        super(e);
    }
}
