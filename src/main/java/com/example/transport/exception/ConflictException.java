package com.example.transport.exception;

/** Conflit (doublon, ressource deja utilisee, chauffeur indisponible...) -> HTTP 409. */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message);
    }
}
