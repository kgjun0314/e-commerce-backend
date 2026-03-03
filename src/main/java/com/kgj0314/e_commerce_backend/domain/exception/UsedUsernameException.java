package com.kgj0314.e_commerce_backend.domain.exception;

public class UsedUsernameException extends RuntimeException {
    public UsedUsernameException(String message) {
        super(message);
    }
}
