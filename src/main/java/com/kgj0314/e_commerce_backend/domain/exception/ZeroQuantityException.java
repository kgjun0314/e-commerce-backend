package com.kgj0314.e_commerce_backend.domain.exception;

public class ZeroQuantityException extends RuntimeException {
    public ZeroQuantityException(String message) {
        super(message);
    }
}
