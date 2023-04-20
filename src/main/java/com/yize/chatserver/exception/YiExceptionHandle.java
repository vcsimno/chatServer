/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLException;

@RestControllerAdvice
public class YiExceptionHandle {

    @ExceptionHandler(YiException.class)
    public void handle(RuntimeException e){

    }

    @ExceptionHandler(Exception.class)
    public void global(Exception e){

    }

    @ExceptionHandler(SQLException.class)
    public void sqlException(SQLException e){

    }
}
