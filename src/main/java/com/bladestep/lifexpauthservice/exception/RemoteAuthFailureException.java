package com.bladestep.lifexpauthservice.exception;

public class RemoteAuthFailureException extends RuntimeException{

    public RemoteAuthFailureException(String message) {
        super(message);
    }
}