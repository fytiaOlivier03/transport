package com.example.transport.service;

import com.example.transport.dto.request.CompleteRequest;
import com.example.transport.dto.request.IncidentRequest;
import com.example.transport.dto.request.MissionRequest;
import com.example.transport.dto.response.CandidatResponse;
import com.example.transport.dto.response.MissionResponse;
import com.example.transport.dto.response.TrackingResponse;
import com.example.transport.entity.Priorite;
import com.example.transport.entity.StatutMission;
import com.example.transport.entity.TypeMission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MissionService {
    MissionResponse creer(MissionRequest request);

    MissionResponse modifier(Long id, MissionRequest request);

    MissionResponse trouver(Long id);

    MissionRequest getRequest(Long id);

    Page<MissionResponse> rechercher(String q, TypeMission type, StatutMission statut, Priorite priorite,
                                     Long chauffeurId, LocalDate from, LocalDate to, Pageable pageable);

    List<MissionResponse> enAttente();

    List<CandidatResponse> candidats(Long id);

    MissionResponse attribuerAutomatiquement(Long id);

    MissionResponse attribuerManuellement(Long id, Long chauffeurId);

    MissionResponse prendreEnCharge(Long id);

    MissionResponse demarrer(Long id);

    MissionResponse terminer(Long id, CompleteRequest preuve);

    MissionResponse annuler(Long id);

    MissionResponse declarerEchec(Long id, IncidentRequest incident);

    MissionResponse retourner(Long id);

    TrackingResponse suivi(Long id);

    void supprimer(Long id);
}
