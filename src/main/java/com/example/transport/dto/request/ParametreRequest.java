package com.example.transport.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ParametreRequest(@NotBlank(message = "La valeur est obligatoire") String valeur) {
}
