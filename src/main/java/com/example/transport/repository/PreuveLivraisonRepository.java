package com.example.transport.repository;

import com.example.transport.entity.PreuveLivraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreuveLivraisonRepository extends JpaRepository<PreuveLivraison, Long> {
    Optional<PreuveLivraison> findByMissionId(Long missionId);
}
