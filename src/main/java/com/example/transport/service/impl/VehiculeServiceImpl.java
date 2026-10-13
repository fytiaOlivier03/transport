package com.example.transport.service.impl;

import com.example.transport.dto.request.VehiculeRequest;
import com.example.transport.dto.response.VehiculeResponse;
import com.example.transport.entity.StatutVehicule;
import com.example.transport.entity.Vehicule;
import com.example.transport.exception.BusinessException;
import com.example.transport.exception.ConflictException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.VehiculeMapper;
import com.example.transport.repository.ChauffeurRepository;
import com.example.transport.repository.MissionRepository;
import com.example.transport.repository.VehiculeRepository;
import com.example.transport.service.VehiculeService;
import com.example.transport.util.RechercheUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class VehiculeServiceImpl implements VehiculeService {

    /** Statuts geres automatiquement par les missions. */
    private static final Set<StatutVehicule> STATUTS_AUTOMATIQUES =
            EnumSet.of(StatutVehicule.EN_COURSE, StatutVehicule.EN_LIVRAISON);

    private final VehiculeRepository vehiculeRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final MissionRepository missionRepository;
    private final VehiculeMapper mapper;

    @Override
    @Transactional
    public VehiculeResponse creer(VehiculeRequest request) {
        if (vehiculeRepository.existsByImmatriculation(request.getImmatriculation().trim())) {
            throw new ConflictException("Cette immatriculation existe déjà : " + request.getImmatriculation());
        }
        Vehicule v = new Vehicule();
        mapper.updateEntity(request, v);
        StatutVehicule statut = request.getStatut() == null ? StatutVehicule.DISPONIBLE : request.getStatut();
        if (STATUTS_AUTOMATIQUES.contains(statut)) {
            throw new BusinessException("Les statuts EN_COURSE / EN_LIVRAISON sont gérés automatiquement");
        }
        v.setStatut(statut);
        return mapper.toResponse(vehiculeRepository.save(v));
    }

    @Override
    @Transactional
    public VehiculeResponse modifier(Long id, VehiculeRequest request) {
        Vehicule v = chercher(id);
        if (vehiculeRepository.existsByImmatriculationAndIdNot(request.getImmatriculation().trim(), id)) {
            throw new ConflictException("Cette immatriculation existe déjà : " + request.getImmatriculation());
        }
        mapper.updateEntity(request, v);
        if (request.getStatut() != null && request.getStatut() != v.getStatut()
                && !STATUTS_AUTOMATIQUES.contains(v.getStatut())) {
            appliquerStatut(v, request.getStatut());
        }
        return mapper.toResponse(vehiculeRepository.save(v));
    }

    @Override
    public VehiculeResponse trouver(Long id) {
        return mapper.toResponse(chercher(id));
    }

    @Override
    public VehiculeRequest getRequest(Long id) {
        return mapper.toRequest(chercher(id));
    }

    @Override
    public Page<VehiculeResponse> rechercher(String q, StatutVehicule statut, Pageable pageable) {
        Specification<Vehicule> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                p.add(cb.or(
                        RechercheUtil.like(cb, root.get("immatriculation"), q),
                        RechercheUtil.like(cb, root.get("marque"), q),
                        RechercheUtil.like(cb, root.get("modele"), q)));
            }
            if (statut != null) {
                p.add(cb.equal(root.get("statut"), statut));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
        return vehiculeRepository.findAll(spec, pageable).map(v -> mapper.toResponse(v));
    }

    @Override
    public List<VehiculeResponse> tous() {
        return vehiculeRepository.findAll(Sort.by("immatriculation")).stream().map(v -> mapper.toResponse(v)).toList();
    }

    @Override
    public List<VehiculeResponse> disponibles() {
        return vehiculeRepository.findByStatut(StatutVehicule.DISPONIBLE).stream().map(v -> mapper.toResponse(v)).toList();
    }

    @Override
    @Transactional
    public VehiculeResponse changerStatut(Long id, StatutVehicule statut) {
        Vehicule v = chercher(id);
        appliquerStatut(v, statut);
        return mapper.toResponse(vehiculeRepository.save(v));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Vehicule v = chercher(id);
        if (chauffeurRepository.existsByVehiculeId(id)) {
            throw new ConflictException("Impossible de supprimer ce véhicule : il est associé à un chauffeur");
        }
        if (missionRepository.existsByVehiculeId(id)) {
            throw new ConflictException("Impossible de supprimer ce véhicule : des missions l'ont utilisé");
        }
        vehiculeRepository.delete(v);
    }

    private void appliquerStatut(Vehicule v, StatutVehicule nouveau) {
        if (STATUTS_AUTOMATIQUES.contains(nouveau)) {
            throw new BusinessException("Les statuts EN_COURSE / EN_LIVRAISON sont gérés automatiquement");
        }
        if (STATUTS_AUTOMATIQUES.contains(v.getStatut())) {
            throw new ConflictException("Ce véhicule est actuellement en mission : son statut ne peut pas être modifié");
        }
        v.setStatut(nouveau);
    }

    private Vehicule chercher(Long id) {
        return vehiculeRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Véhicule", id));
    }
}
