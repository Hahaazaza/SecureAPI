package com.example.SecureAPI.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {

        super(message);
    }
}
