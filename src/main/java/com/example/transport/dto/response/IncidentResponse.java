package com.example.transport.dto.response;

import com.example.transport.entity.TypeIncident;

import java.time.LocalDateTime;

public record IncidentResponse(
        Long id, TypeIncident type, LocalDateTime dateIncident, String description,
        Long missionId, String missionNumero, Long chauffeurId, String chauffeurNom,
        String resolution, boolean resolu) {
}
