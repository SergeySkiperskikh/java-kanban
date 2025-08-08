package com.yandex.app.Exceptions;

public class RequestFormatException extends RuntimeException {
    public RequestFormatException(String message) {
        super(message);
    }
}
