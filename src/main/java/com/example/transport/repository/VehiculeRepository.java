package com.example.transport.repository;

import com.example.transport.entity.StatutVehicule;
import com.example.transport.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long>, JpaSpecificationExecutor<Vehicule> {
    boolean existsByImmatriculation(String immatriculation);

    boolean existsByImmatriculationAndIdNot(String immatriculation, Long id);

    List<Vehicule> findByStatut(StatutVehicule statut);

    long countByStatut(StatutVehicule statut);
}
