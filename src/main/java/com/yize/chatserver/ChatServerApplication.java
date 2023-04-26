package com.yize.chatserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan( basePackages = "com.yize.chatserver.mapper")
public class ChatServerApplication {

    public static void main(String[] args) {
        String[] param = new String[args.length+1];
        int index = 0;
        for(int i = 0; i < args.length; ++i){
            param[i] = args[i];
            ++index;
        }
        param[index] = "--mpw.key="+"04dc7e59eeaa8ab6";
        SpringApplication.run(ChatServerApplication.class, param);
    }

}
