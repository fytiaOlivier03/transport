package com.example.transport.exception;

/** Regle metier non respectee -> HTTP 400. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
