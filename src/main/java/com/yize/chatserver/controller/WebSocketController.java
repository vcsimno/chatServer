package com.yize.chatserver.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Service
@ServerEndpoint(value = "/cs_server")
@DependsOn({"springUtils"})
public class WebSocketController {

    public WebSocketController(){

    }

    private  final Logger logger = LogManager.getLogger(WebSocketController.class);

    @OnOpen
    public void onOpen () {

    }


    @OnClose
    public void onClose (Session session) throws IOException {
        if(session.isOpen()) {
            session.close();
        }
    }

    @OnMessage
    public void onMessage (String message, Session session) {

    }

    @OnError
    public void onError (Session session, Throwable error) {
        try {
            onClose(session);
        }catch (Throwable e) {
           logger.error(e.getMessage());
        }
    }
}
