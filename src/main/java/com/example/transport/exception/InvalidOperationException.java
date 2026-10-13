package com.example.transport.exception;

/** Operation impossible dans l'etat actuel (ex : transition de statut interdite) -> HTTP 422. */
public class InvalidOperationException extends BusinessException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
