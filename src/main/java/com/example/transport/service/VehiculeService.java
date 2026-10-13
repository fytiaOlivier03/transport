package com.example.transport.service;

import com.example.transport.dto.request.VehiculeRequest;
import com.example.transport.dto.response.VehiculeResponse;
import com.example.transport.entity.StatutVehicule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehiculeService {
    VehiculeResponse creer(VehiculeRequest request);

    VehiculeResponse modifier(Long id, VehiculeRequest request);

    VehiculeResponse trouver(Long id);

    VehiculeRequest getRequest(Long id);

    Page<VehiculeResponse> rechercher(String q, StatutVehicule statut, Pageable pageable);

    List<VehiculeResponse> tous();

    List<VehiculeResponse> disponibles();

    VehiculeResponse changerStatut(Long id, StatutVehicule statut);

    void supprimer(Long id);
}
