package com.example.transport.repository;

import com.example.transport.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByMissionIdOrderByDateIncidentDesc(Long missionId);

    Page<Incident> findByResolu(boolean resolu, Pageable pageable);

    void deleteByMissionId(Long missionId);
}
