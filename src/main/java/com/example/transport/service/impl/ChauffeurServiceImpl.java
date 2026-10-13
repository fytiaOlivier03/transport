package com.example.transport.service.impl;

import com.example.transport.dto.request.ChauffeurRequest;
import com.example.transport.dto.response.ChauffeurResponse;
import com.example.transport.entity.Chauffeur;
import com.example.transport.entity.StatutChauffeur;
import com.example.transport.entity.Vehicule;
import com.example.transport.exception.BusinessException;
import com.example.transport.exception.ConflictException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.ChauffeurMapper;
import com.example.transport.repository.ChauffeurRepository;
import com.example.transport.repository.MissionRepository;
import com.example.transport.repository.VehiculeRepository;
import com.example.transport.service.ChauffeurService;
import com.example.transport.util.RechercheUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class ChauffeurServiceImpl implements ChauffeurService {

    /** Statuts geres automatiquement par les missions. */
    private static final Set<StatutChauffeur> STATUTS_AUTOMATIQUES =
            EnumSet.of(StatutChauffeur.EN_COURSE, StatutChauffeur.EN_LIVRAISON);

    private final ChauffeurRepository chauffeurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final MissionRepository missionRepository;
    private final ChauffeurMapper mapper;

    @Override
    @Transactional
    public ChauffeurResponse creer(ChauffeurRequest request) {
        if (chauffeurRepository.existsByNumeroPermis(request.getNumeroPermis().trim())) {
            throw new ConflictException("Ce numéro de permis existe déjà : " + request.getNumeroPermis());
        }
        Chauffeur c = new Chauffeur();
        mapper.updateEntity(request, c);
        c.setVehicule(resoudreVehicule(request.getVehiculeId(), null));
        StatutChauffeur statut = request.getStatut() == null ? StatutChauffeur.DISPONIBLE : request.getStatut();
        if (STATUTS_AUTOMATIQUES.contains(statut)) {
            throw new BusinessException("Les statuts EN_COURSE / EN_LIVRAISON sont gérés automatiquement");
        }
        c.setStatut(statut);
        return mapper.toResponse(chauffeurRepository.save(c));
    }

    @Override
    @Transactional
    public ChauffeurResponse modifier(Long id, ChauffeurRequest request) {
        Chauffeur c = chercher(id);
        if (chauffeurRepository.existsByNumeroPermisAndIdNot(request.getNumeroPermis().trim(), id)) {
            throw new ConflictException("Ce numéro de permis existe déjà : " + request.getNumeroPermis());
        }
        Vehicule nouveauVehicule = resoudreVehicule(request.getVehiculeId(), id);
        Long ancienId = c.getVehicule() == null ? null : c.getVehicule().getId();
        Long nouveauId = nouveauVehicule == null ? null : nouveauVehicule.getId();
        boolean enMission = STATUTS_AUTOMATIQUES.contains(c.getStatut());
        if (enMission && !Objects.equals(ancienId, nouveauId)) {
            throw new ConflictException("Impossible de changer le véhicule d'un chauffeur en mission");
        }
        mapper.updateEntity(request, c);
        c.setVehicule(nouveauVehicule);
        if (!enMission && request.getStatut() != null && request.getStatut() != c.getStatut()) {
            appliquerStatut(c, request.getStatut());
        }
        return mapper.toResponse(chauffeurRepository.save(c));
    }

    @Override
    public ChauffeurResponse trouver(Long id) {
        return mapper.toResponse(chercher(id));
    }

    @Override
    public ChauffeurRequest getRequest(Long id) {
        return mapper.toRequest(chercher(id));
    }

    @Override
    public Page<ChauffeurResponse> rechercher(String q, StatutChauffeur statut, String zone, Pageable pageable) {
        Specification<Chauffeur> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                p.add(cb.or(
                        RechercheUtil.like(cb, root.get("nom"), q),
                        RechercheUtil.like(cb, root.get("prenom"), q),
                        RechercheUtil.like(cb, root.get("telephone"), q),
                        RechercheUtil.like(cb, root.get("numeroPermis"), q)));
            }
            if (statut != null) {
                p.add(cb.equal(root.get("statut"), statut));
            }
            if (zone != null && !zone.isBlank()) {
                p.add(cb.equal(cb.lower(root.get("zone")), zone.trim().toLowerCase()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
        return chauffeurRepository.findAll(spec, pageable).map(c -> mapper.toResponse(c));
    }

    @Override
    public List<ChauffeurResponse> disponibles() {
        return chauffeurRepository.findByStatut(StatutChauffeur.DISPONIBLE).stream()
                .map(c -> mapper.toResponse(c)).toList();
    }

    @Override
    @Transactional
    public ChauffeurResponse changerStatut(Long id, StatutChauffeur statut) {
        Chauffeur c = chercher(id);
        appliquerStatut(c, statut);
        return mapper.toResponse(chauffeurRepository.save(c));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Chauffeur c = chercher(id);
        if (missionRepository.existsByChauffeurId(id)) {
            throw new ConflictException("Impossible de supprimer ce chauffeur : des missions lui sont associées");
        }
        chauffeurRepository.delete(c);
    }

    private void appliquerStatut(Chauffeur c, StatutChauffeur nouveau) {
        if (STATUTS_AUTOMATIQUES.contains(nouveau)) {
            throw new BusinessException("Les statuts EN_COURSE / EN_LIVRAISON sont gérés automatiquement");
        }
        if (STATUTS_AUTOMATIQUES.contains(c.getStatut())) {
            throw new ConflictException("Ce chauffeur est actuellement en mission : son statut ne peut pas être modifié");
        }
        c.setStatut(nouveau);
    }

    private Vehicule resoudreVehicule(Long vehiculeId, Long chauffeurId) {
        if (vehiculeId == null) {
            return null;
        }
        Vehicule v = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> ResourceNotFoundException.of("Véhicule", vehiculeId));
        boolean dejaPris = chauffeurId == null
                ? chauffeurRepository.existsByVehiculeId(vehiculeId)
                : chauffeurRepository.existsByVehiculeIdAndIdNot(vehiculeId, chauffeurId);
        if (dejaPris) {
            throw new ConflictException("Ce véhicule est déjà associé à un autre chauffeur");
        }
        return v;
    }

    private Chauffeur chercher(Long id) {
        return chauffeurRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Chauffeur", id));
    }
}
