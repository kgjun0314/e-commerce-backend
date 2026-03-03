package com.kgj0314.e_commerce_backend.domain.exception;

public class UsedEmailException extends RuntimeException {
    public UsedEmailException(String message) {
        super(message);
    }
}
