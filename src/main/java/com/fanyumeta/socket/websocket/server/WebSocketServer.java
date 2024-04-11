package com.fanyumeta.socket.websocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;

@Slf4j
@Component
@ServerEndpoint("/websocket")
public class WebSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        log.info("客户端：已连接");
    }

    /**
     * 处理客户端发来的字符串消息
     * @param session websocket 会话
     * @param message 接收到的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("接收到客户端的【字符串】消息：{}", message);
    }

    /**
     * 处理客户端发来的数据流消息
     * @param session websocket 会话
     * @param message 接收到的消息
     */
    @OnMessage
    public void onMessage(Session session, ByteBuffer message) {
        log.info("接收到客户端的【二进制】消息：{}", message);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("客户端断开连接");
    }
}
