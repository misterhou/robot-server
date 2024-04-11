package com.fanyumeta.socket.exception;

/**
 * Socket 异常
 */
public class SocketException extends Exception {

    public SocketException(String message) {
        super(message);
    }

    public SocketException(String message, Throwable e) {
        super(message, e);
    }
}
