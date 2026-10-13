package com.example.transport.dto.request;

import com.example.transport.entity.StatutVehicule;
import jakarta.validation.constraints.NotNull;

public record StatutVehiculeRequest(@NotNull(message = "Le statut est obligatoire") StatutVehicule statut) {
}
