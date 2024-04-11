package com.fanyumeta.socket.websocket.message;

import java.nio.ByteBuffer;

/**
 * 接收 websocket 服务消息
 */
public interface WebSocketResponseMessageHandler {

    /**
     * 接收字符串消息
     * @param message 字符串消息
     */
    void handler(String message);

    /**
     * 接收二进制消息
     * @param message 二进制消息
     */
    void handler(ByteBuffer message);
}
