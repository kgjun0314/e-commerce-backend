package com.kgj0314.e_commerce_backend.domain.exception;

public class NotEnoughQuantityException extends RuntimeException {
    public NotEnoughQuantityException(String message) {
        super(message);
    }
}
