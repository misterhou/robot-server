package com.fanyumeta.socket.websocket.client;


import com.fanyumeta.socket.websocket.message.WebSocketResponseMessageHandler;
import com.fanyumeta.socket.websocket.exception.WebSocketException;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.net.URI;
import java.nio.ByteBuffer;

@Slf4j
@ClientEndpoint
public class WebSocketClient {

    private WebSocketResponseMessageHandler webSocketResponseMessageHandler;

    public WebSocketClient(String serverAddr, WebSocketResponseMessageHandler handler) throws WebSocketException {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        try {
            webSocketContainer.connectToServer(this, new URI(serverAddr));
        } catch (Exception e) {
            throw new WebSocketException("websocket 客户端初始化异常：" + e.getMessage(), e);
        }
        this.webSocketResponseMessageHandler = handler;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("websocket 连接【已建立】");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("接收到 websocket 服务的【字符串】消息：{}", message);
        this.webSocketResponseMessageHandler.handler(message);
    }

    @OnMessage
    public void onMessage(Session session, ByteBuffer message) {
        log.info("接收到 websocket 服务的【二进制】消息：{}", message);
        this.webSocketResponseMessageHandler.handler(message);
    }
    @OnClose
    public void onClose(Session session) {
        log.info("websocket 连接【已断开】");
    }
}
