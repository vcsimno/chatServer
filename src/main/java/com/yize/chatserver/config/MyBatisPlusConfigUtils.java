/*
 * Copyright (c) 2022. yize.link
 * editor: yize
 * date:  2022/11/8
 *
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */
package com.yize.chatserver.config;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.AES;
import lombok.Data;


public class MyBatisPlusConfigUtils {

    public MyBatisPlusConfigUtils(){
        dbList = new JSONArray();
        randomKey = AES.generateRandomKey();
    }

    @Data
    static
    class Dbs{
        private String url;
        private String user;
        private String pwd;

        public String toString(){
            return "url=" + url
                    + ", user=" + user
                    + ", pwd =" + pwd;
        }
    }

    public void insert(String url, String user, String pwd){
        Dbs db = new Dbs();
        db.setUrl("mpw:"+AES.encrypt(url, randomKey));
        db.setUser("mpw:"+AES.encrypt(user, randomKey));
        db.setPwd("mpw:"+AES.encrypt(pwd, randomKey));
        dbList.add(db);
    }

    private final JSONArray dbList;
    private final String randomKey;

    public void printf(){
        System.out.println("key="+randomKey);
        dbList.forEach(e->{
            System.out.println("e = " + e);
        });
    }
}
