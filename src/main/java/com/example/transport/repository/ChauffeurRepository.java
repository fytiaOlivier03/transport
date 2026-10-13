package com.example.transport.repository;

import com.example.transport.entity.Chauffeur;
import com.example.transport.entity.StatutChauffeur;
import com.example.transport.entity.StatutVehicule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long>, JpaSpecificationExecutor<Chauffeur> {

    boolean existsByNumeroPermis(String numeroPermis);

    boolean existsByNumeroPermisAndIdNot(String numeroPermis, Long id);

    boolean existsByVehiculeId(Long vehiculeId);

    boolean existsByVehiculeIdAndIdNot(Long vehiculeId, Long id);

    List<Chauffeur> findByStatut(StatutChauffeur statut);

    long countByStatut(StatutChauffeur statut);

    /** Verrou pessimiste : evite que deux attributions simultanees prennent le meme chauffeur. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Chauffeur c where c.id = :id")
    Optional<Chauffeur> findByIdForUpdate(@Param("id") Long id);

    @Query("select c from Chauffeur c join c.vehicule v where c.statut = :statut and v.statut = :statutVehicule")
    List<Chauffeur> findCandidats(@Param("statut") StatutChauffeur statut,
                                  @Param("statutVehicule") StatutVehicule statutVehicule);
}
