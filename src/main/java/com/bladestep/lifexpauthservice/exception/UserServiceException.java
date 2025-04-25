package com.bladestep.lifexpauthservice.exception;

import org.springframework.http.HttpStatusCode;

public class UserServiceException extends RuntimeException{

    private final HttpStatusCode statusCode;

    public UserServiceException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}