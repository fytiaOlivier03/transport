package com.example.transport.dto.response;

import com.example.transport.entity.StatutChauffeur;

import java.time.LocalDate;

public record ChauffeurResponse(
        Long id, String nom, String prenom, String telephone,
        String numeroPermis, LocalDate dateExpirationPermis, boolean permisValide,
        LocalDate dateEmbauche, String zone, Double latitude, Double longitude,
        StatutChauffeur statut, Long vehiculeId, String vehiculeImmatriculation) {
}
