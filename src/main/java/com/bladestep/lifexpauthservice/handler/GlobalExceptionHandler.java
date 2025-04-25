package com.bladestep.lifexpauthservice.handler;

import com.bladestep.lifexpauthservice.exception.RemoteAuthFailureException;
import com.bladestep.lifexpauthservice.exception.UserNotFoundException;
import com.bladestep.lifexpauthservice.exception.UserServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RemoteAuthFailureException.class)
    public ResponseEntity<String> handleAuthServiceException(RemoteAuthFailureException ex) {
        log.warn("Auth error: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<String> handleUserServiceException(UserServiceException ex) {
        log.error("User service error: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode() != null ? ex.getStatusCode() : HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unhandled error: ", ex);
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}