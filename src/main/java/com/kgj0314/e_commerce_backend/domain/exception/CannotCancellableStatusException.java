package com.kgj0314.e_commerce_backend.domain.exception;

public class CannotCancellableStatusException extends RuntimeException {
    public CannotCancellableStatusException(String message) {
        super(message);
    }
}
