package com.example.transport.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolutionRequest(
        @NotBlank(message = "La résolution est obligatoire") @Size(max = 1000) String resolution) {
}
