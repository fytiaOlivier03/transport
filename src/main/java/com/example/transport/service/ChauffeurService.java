package com.example.transport.service;

import com.example.transport.dto.request.ChauffeurRequest;
import com.example.transport.dto.response.ChauffeurResponse;
import com.example.transport.entity.StatutChauffeur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChauffeurService {
    ChauffeurResponse creer(ChauffeurRequest request);

    ChauffeurResponse modifier(Long id, ChauffeurRequest request);

    ChauffeurResponse trouver(Long id);

    ChauffeurRequest getRequest(Long id);

    Page<ChauffeurResponse> rechercher(String q, StatutChauffeur statut, String zone, Pageable pageable);

    List<ChauffeurResponse> disponibles();

    ChauffeurResponse changerStatut(Long id, StatutChauffeur statut);

    void supprimer(Long id);
}
