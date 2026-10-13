package com.example.transport.repository;

import com.example.transport.entity.Mission;
import com.example.transport.entity.StatutMission;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Mission m where m.id = :id")
    Optional<Mission> findByIdForUpdate(@Param("id") Long id);

    List<Mission> findByStatutOrderByDatePrevueAsc(StatutMission statut);

    long countByStatut(StatutMission statut);

    long countByStatutIn(Collection<StatutMission> statuts);

    boolean existsByChauffeurIdAndStatutIn(Long chauffeurId, Collection<StatutMission> statuts);

    long countByChauffeurIdAndDatePrevueBetween(Long chauffeurId, LocalDateTime debut, LocalDateTime fin);

    List<Mission> findByStatutAndDateFinBetween(StatutMission statut, LocalDateTime debut, LocalDateTime fin);

    boolean existsByClientId(Long clientId);

    boolean existsByChauffeurId(Long chauffeurId);

    boolean existsByVehiculeId(Long vehiculeId);
}
