package com.aman.taskmanager.exception;

public class JwtTokenExpiredException extends RuntimeException {

    // Constructor to initialize the exception with a custom message
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
