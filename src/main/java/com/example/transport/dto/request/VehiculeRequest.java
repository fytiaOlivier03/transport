package com.example.transport.dto.request;

import com.example.transport.entity.StatutVehicule;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehiculeRequest {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(max = 30, message = "L'immatriculation ne doit pas dépasser 30 caractères")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 100)
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100)
    private String modele;

    @Min(value = 1950, message = "Année invalide")
    @Max(value = 2100, message = "Année invalide")
    private Integer annee;

    @NotNull(message = "La capacité en places est obligatoire")
    @Positive(message = "La capacité en places doit être positive")
    private Integer capacitePlaces;

    @NotNull(message = "La capacité de charge (kg) est obligatoire")
    @Positive(message = "La capacité de charge doit être positive")
    private Double capaciteKg;

    @PositiveOrZero(message = "Le kilométrage ne peut pas être négatif")
    private Double kilometrage = 0.0;

    private StatutVehicule statut;
}
