package com.kgj0314.e_commerce_backend.domain.exception;

public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException(String message) {
        super(message);
    }
}
