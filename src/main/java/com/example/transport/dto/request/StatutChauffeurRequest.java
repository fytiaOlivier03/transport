package com.example.transport.dto.request;

import com.example.transport.entity.StatutChauffeur;
import jakarta.validation.constraints.NotNull;

public record StatutChauffeurRequest(@NotNull(message = "Le statut est obligatoire") StatutChauffeur statut) {
}
