package com.fanyumeta.socket.exception;

/**
 * http 异常
 */
public class HttpException extends SocketException {

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable e) {
        super(message, e);
    }
}
