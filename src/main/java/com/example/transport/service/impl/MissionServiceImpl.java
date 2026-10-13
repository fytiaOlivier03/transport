package com.example.transport.service.impl;

import com.example.transport.dto.request.CompleteRequest;
import com.example.transport.dto.request.IncidentRequest;
import com.example.transport.dto.request.MissionRequest;
import com.example.transport.dto.response.CandidatResponse;
import com.example.transport.dto.response.MissionResponse;
import com.example.transport.dto.response.TrackingResponse;
import com.example.transport.entity.*;
import com.example.transport.exception.BusinessException;
import com.example.transport.exception.ConflictException;
import com.example.transport.exception.InvalidOperationException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.MissionMapper;
import com.example.transport.repository.*;
import com.example.transport.service.AffectationService;
import com.example.transport.service.MissionService;
import com.example.transport.service.TarificationService;
import com.example.transport.util.NumeroUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final PreuveLivraisonRepository preuveRepository;
    private final IncidentRepository incidentRepository;
    private final MissionMapper mapper;
    private final TarificationService tarificationService;
    private final AffectationService affectationService;

    // ------------------------------------------------------------------ CRUD

    @Override
    @Transactional
    public MissionResponse creer(MissionRequest request) {
        Mission m = new Mission();
        appliquer(request, m);
        m.setNumero(NumeroUtil.generer(request.getType()));
        m.setStatut(StatutMission.CREEE);
        m.setDateCreation(LocalDateTime.now());
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    @Transactional
    public MissionResponse modifier(Long id, MissionRequest request) {
        Mission m = chercher(id);
        if (m.getStatut() != StatutMission.CREEE) {
            throw new InvalidOperationException("Seule une mission au statut CREEE peut être modifiée (statut actuel : "
                    + m.getStatut() + ")");
        }
        appliquer(request, m);
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    public MissionResponse trouver(Long id) {
        Mission m = chercher(id);
        return mapper.toDetailResponse(m, preuveRepository.findByMissionId(id).orElse(null));
    }

    @Override
    public MissionRequest getRequest(Long id) {
        return mapper.toRequest(chercher(id));
    }

    @Override
    public Page<MissionResponse> rechercher(String q, TypeMission type, StatutMission statut, Priorite priorite,
                                            Long chauffeurId, LocalDate from, LocalDate to, Pageable pageable) {
        LocalDateTime debut = from == null ? null : from.atStartOfDay();
        LocalDateTime fin = to == null ? null : to.atTime(LocalTime.MAX);
        return missionRepository
                .findAll(MissionSpecs.filtre(q, type, statut, priorite, chauffeurId, null, debut, fin), pageable)
                .map(m -> mapper.toResponse(m));
    }

    @Override
    public List<MissionResponse> enAttente() {
        return missionRepository.findByStatutOrderByDatePrevueAsc(StatutMission.CREEE).stream()
                .sorted(Comparator.comparing(Mission::getPriorite).reversed())
                .map(m -> mapper.toResponse(m))
                .toList();
    }

    @Override
    public List<CandidatResponse> candidats(Long id) {
        Mission m = chercher(id);
        if (m.getStatut() != StatutMission.CREEE) {
            return List.of();
        }
        return affectationService.classer(m);
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Mission m = chercher(id);
        if (m.getStatut() != StatutMission.CREEE && m.getStatut() != StatutMission.ANNULEE) {
            throw new InvalidOperationException("Seule une mission CREEE ou ANNULEE peut être supprimée");
        }
        incidentRepository.deleteByMissionId(id);
        missionRepository.delete(m);
    }

    // ------------------------------------------------------------ ATTRIBUTION

    @Override
    @Transactional
    public MissionResponse attribuerAutomatiquement(Long id) {
        Mission m = chercherPourMiseAJour(id);
        verifierAttribuable(m);
        List<CandidatResponse> classement = affectationService.classer(m);
        if (classement.isEmpty()) {
            throw new BusinessException("Aucun chauffeur éligible pour cette mission pour le moment");
        }
        Long meilleurId = classement.get(0).chauffeurId();
        Chauffeur meilleur = chauffeurRepository.findByIdForUpdate(meilleurId)
                .orElseThrow(() -> ResourceNotFoundException.of("Chauffeur", meilleurId));
        affecter(m, meilleur);
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    @Transactional
    public MissionResponse attribuerManuellement(Long id, Long chauffeurId) {
        Mission m = chercherPourMiseAJour(id);
        verifierAttribuable(m);
        Chauffeur c = chauffeurRepository.findByIdForUpdate(chauffeurId)
                .orElseThrow(() -> ResourceNotFoundException.of("Chauffeur", chauffeurId));
        affecter(m, c);
        return mapper.toResponse(missionRepository.save(m));
    }

    private void verifierAttribuable(Mission m) {
        if (m.getChauffeur() != null || m.getStatut() == StatutMission.ASSIGNEE) {
            throw new ConflictException("Cette mission est déjà attribuée à un chauffeur");
        }
        if (m.getStatut() != StatutMission.CREEE) {
            throw new InvalidOperationException("Une mission au statut " + m.getStatut() + " ne peut pas être attribuée");
        }
    }

    /** Operation transactionnelle : verifie, lie mission/chauffeur/vehicule et change les statuts. */
    private void affecter(Mission m, Chauffeur c) {
        affectationService.verifierEligibilite(m, c);
        transition(m, StatutMission.ASSIGNEE);
        boolean course = m.getType() == TypeMission.COURSE;
        m.setChauffeur(c);
        m.setVehicule(c.getVehicule());
        m.setDateAssignation(LocalDateTime.now());
        c.setStatut(course ? StatutChauffeur.EN_COURSE : StatutChauffeur.EN_LIVRAISON);
        c.getVehicule().setStatut(course ? StatutVehicule.EN_COURSE : StatutVehicule.EN_LIVRAISON);
    }

    // --------------------------------------------------------------- WORKFLOW

    @Override
    @Transactional
    public MissionResponse prendreEnCharge(Long id) {
        Mission m = chercherPourMiseAJour(id);
        transition(m, StatutMission.PRISE_EN_CHARGE);
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    @Transactional
    public MissionResponse demarrer(Long id) {
        Mission m = chercherPourMiseAJour(id);
        transition(m, StatutMission.EN_COURS);
        m.setDateDebut(LocalDateTime.now());
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    @Transactional
    public MissionResponse terminer(Long id, CompleteRequest preuve) {
        Mission m = chercherPourMiseAJour(id);
        transition(m, StatutMission.TERMINEE);
        if (m.getType() == TypeMission.LIVRAISON) {
            if (preuve == null || estVide(preuve.getNomReceptionnaire()) || estVide(preuve.getCodeConfirmation())) {
                throw new BusinessException(
                        "Preuve de livraison obligatoire : nom du réceptionnaire et code de confirmation");
            }
            PreuveLivraison p = new PreuveLivraison();
            p.setMission(m);
            p.setDateHeure(LocalDateTime.now());
            p.setNomReceptionnaire(preuve.getNomReceptionnaire().trim());
            p.setCodeConfirmation(preuve.getCodeConfirmation().trim());
            p.setCommentaire(preuve.getCommentaire());
            preuveRepository.save(p);
        }
        m.setDateFin(LocalDateTime.now());
        m.setCommission(tarificationService.calculerCommission(m.getPrix()));
        if (m.getVehicule() != null) {
            double km = m.getVehicule().getKilometrage() == null ? 0.0 : m.getVehicule().getKilometrage();
            m.getVehicule().setKilometrage(km + m.getDistanceKm());
        }
        libererRessources(m);
        return mapper.toDetailResponse(missionRepository.save(m), preuveRepository.findByMissionId(id).orElse(null));
    }

    @Override
    @Transactional
    public MissionResponse annuler(Long id) {
        Mission m = chercherPourMiseAJour(id);
        transition(m, StatutMission.ANNULEE);
        m.setDateFin(LocalDateTime.now());
        libererRessources(m);
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    @Transactional
    public MissionResponse declarerEchec(Long id, IncidentRequest request) {
        Mission m = chercherPourMiseAJour(id);
        transition(m, StatutMission.ECHEC);
        Incident incident = new Incident();
        incident.setType(request.getType());
        incident.setDescription(request.getDescription());
        incident.setDateIncident(LocalDateTime.now());
        incident.setMission(m);
        incident.setChauffeur(m.getChauffeur());
        incidentRepository.save(incident);
        libererRessources(m);
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    @Transactional
    public MissionResponse retourner(Long id) {
        Mission m = chercherPourMiseAJour(id);
        if (m.getType() != TypeMission.LIVRAISON) {
            throw new InvalidOperationException("Seule une livraison peut être retournée à l'expéditeur");
        }
        transition(m, StatutMission.RETOURNEE);
        m.setDateFin(LocalDateTime.now());
        return mapper.toResponse(missionRepository.save(m));
    }

    @Override
    public TrackingResponse suivi(Long id) {
        return mapper.toTracking(chercher(id));
    }

    // ----------------------------------------------------------------- OUTILS

    /** Applique une transition du workflow ; les transitions incoherentes sont refusees. */
    private void transition(Mission m, StatutMission cible) {
        if (!m.getStatut().peutPasserA(cible)) {
            throw new InvalidOperationException("Transition interdite : " + m.getStatut() + " -> " + cible);
        }
        m.setStatut(cible);
    }

    private void libererRessources(Mission m) {
        Chauffeur c = m.getChauffeur();
        if (c != null && (c.getStatut() == StatutChauffeur.EN_COURSE || c.getStatut() == StatutChauffeur.EN_LIVRAISON)) {
            c.setStatut(StatutChauffeur.DISPONIBLE);
        }
        Vehicule v = m.getVehicule();
        if (v != null && (v.getStatut() == StatutVehicule.EN_COURSE || v.getStatut() == StatutVehicule.EN_LIVRAISON)) {
            v.setStatut(StatutVehicule.DISPONIBLE);
        }
    }

    private void appliquer(MissionRequest r, Mission m) {
        if (r.getType() == TypeMission.LIVRAISON && r.getPoids() == null) {
            throw new BusinessException("Le poids est obligatoire pour une livraison");
        }
        Client client = clientRepository.findById(r.getClientId())
                .orElseThrow(() -> ResourceNotFoundException.of("Client", r.getClientId()));
        mapper.updateEntity(r, m);
        m.setClient(client);
        m.setPriorite(r.getPriorite() == null ? Priorite.NORMALE : r.getPriorite());
        if (r.getType() == TypeMission.COURSE) {
            m.setPoids(null);
            m.setVolume(null);
            m.setNombrePassagers(r.getNombrePassagers() == null ? 1 : r.getNombrePassagers());
        } else {
            m.setNombrePassagers(null);
        }
        m.setPrix(tarificationService.calculerPrix(
                m.getType(), m.getDistanceKm(), m.getPoids(), m.getVolume(), m.getPriorite()));
    }

    private boolean estVide(String s) {
        return s == null || s.isBlank();
    }

    private Mission chercher(Long id) {
        return missionRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Mission", id));
    }

    private Mission chercherPourMiseAJour(Long id) {
        return missionRepository.findByIdForUpdate(id).orElseThrow(() -> ResourceNotFoundException.of("Mission", id));
    }
}
