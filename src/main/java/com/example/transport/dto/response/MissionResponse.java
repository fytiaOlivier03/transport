package com.example.transport.dto.response;

import com.example.transport.entity.Priorite;
import com.example.transport.entity.StatutMission;
import com.example.transport.entity.TypeMission;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MissionResponse(
        Long id, String numero, TypeMission type,
        Long clientId, String clientNom,
        String adresseDepart, String adresseDestination, String zone,
        Double distanceKm, Double poids, Double volume, Integer nombrePassagers,
        Priorite priorite, LocalDateTime datePrevue,
        BigDecimal prix, BigDecimal commission, StatutMission statut,
        Long chauffeurId, String chauffeurNom, Long vehiculeId, String vehiculeImmatriculation,
        LocalDateTime dateCreation, LocalDateTime dateAssignation, LocalDateTime dateDebut, LocalDateTime dateFin,
        PreuveResponse preuve) {
}
