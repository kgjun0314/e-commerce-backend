package com.kgj0314.e_commerce_backend.domain.exception;

public class MemberIdMismatchException extends RuntimeException {
    public MemberIdMismatchException(String message) {
        super(message);
    }
}
