package com.example.transport.dto.response;

/** Chauffeur eligible pour une mission, avec son score d'attribution. */
public record CandidatResponse(
        Long chauffeurId, String chauffeur, String vehicule, String zone,
        Double distanceKm, long missionsDuJour, double score) {
}
