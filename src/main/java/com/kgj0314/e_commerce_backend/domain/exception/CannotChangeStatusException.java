package com.kgj0314.e_commerce_backend.domain.exception;

public class CannotChangeStatusException extends RuntimeException {
    public CannotChangeStatusException(String message) {
        super(message);
    }
}
