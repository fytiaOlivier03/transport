package com.example.transport.dto.response;

import com.example.transport.entity.StatutVehicule;

public record VehiculeResponse(
        Long id, String immatriculation, String marque, String modele, Integer annee,
        Integer capacitePlaces, Double capaciteKg, Double kilometrage, StatutVehicule statut) {
}
