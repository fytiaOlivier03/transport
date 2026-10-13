package com.example.transport.exception;

import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Messages d'erreur comprehensibles pour l'interface Thymeleaf. */
@ControllerAdvice(basePackages = "com.example.transport.controller.web")
public class GlobalWebExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(ResourceNotFoundException ex, Model model) {
        return page(model, 404, "Ressource introuvable", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict(ConflictException ex, Model model) {
        return page(model, 409, "Conflit", ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String business(BusinessException ex, Model model) {
        return page(model, 400, "Opération impossible", ex.getMessage());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badSort(PropertyReferenceException ex, Model model) {
        return page(model, 400, "Requête invalide", "Critère de tri inconnu : " + ex.getPropertyName());
    }

    private String page(Model model, int status, String error, String message) {
        model.addAttribute("status", status);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return "error";
    }
}
