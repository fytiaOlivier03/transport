package com.example.transport.service;

import com.example.transport.dto.response.CandidatResponse;
import com.example.transport.entity.Chauffeur;
import com.example.transport.entity.Mission;

import java.util.List;

public interface AffectationService {

    /** Retourne le motif d'inegibilite, ou null si le chauffeur peut recevoir la mission. */
    String motifIneligibilite(Mission mission, Chauffeur chauffeur);

    /** Lance une ConflictException si le chauffeur ne peut pas recevoir la mission. */
    void verifierEligibilite(Mission mission, Chauffeur chauffeur);

    /** Chauffeurs eligibles classes par score decroissant. */
    List<CandidatResponse> classer(Mission mission);
}
