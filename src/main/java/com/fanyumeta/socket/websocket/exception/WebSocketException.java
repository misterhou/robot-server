package com.fanyumeta.socket.websocket.exception;

/**
 * websocket 异常
 */
public class WebSocketException extends Exception {

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable e) {
        super(message, e);
    }
}
