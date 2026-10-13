package com.example.transport.dto.response;

import com.example.transport.entity.StatutMission;
import com.example.transport.entity.TypeMission;

import java.time.LocalDateTime;

public record TrackingResponse(
        String numero, TypeMission type, StatutMission statut, String chauffeur, String vehicule,
        LocalDateTime dateCreation, LocalDateTime dateAssignation, LocalDateTime dateDebut, LocalDateTime dateFin) {
}
