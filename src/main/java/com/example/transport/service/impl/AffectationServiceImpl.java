package com.example.transport.service.impl;

import com.example.transport.dto.response.CandidatResponse;
import com.example.transport.entity.*;
import com.example.transport.exception.ConflictException;
import com.example.transport.repository.ChauffeurRepository;
import com.example.transport.repository.MissionRepository;
import com.example.transport.service.AffectationService;
import com.example.transport.service.ParametreService;
import com.example.transport.util.GeoUtil;
import com.example.transport.util.ParametreCles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Moteur d'attribution : eligibilite + score.
 * score = coefDistance x facteurPriorite x 1/(1+distance) + coefZone x memeZone + coefCharge x 1/(1+missionsDuJour)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class AffectationServiceImpl implements AffectationService {

    private static final Set<StatutMission> ACTIFS =
            EnumSet.of(StatutMission.ASSIGNEE, StatutMission.PRISE_EN_CHARGE, StatutMission.EN_COURS);

    private final ChauffeurRepository chauffeurRepository;
    private final MissionRepository missionRepository;
    private final ParametreService parametreService;

    @Override
    public String motifIneligibilite(Mission mission, Chauffeur chauffeur) {
        if (chauffeur.getStatut() == StatutChauffeur.SUSPENDU) {
            return "le chauffeur est suspendu";
        }
        if (chauffeur.getStatut() != StatutChauffeur.DISPONIBLE) {
            return "le chauffeur n'est pas disponible (statut " + chauffeur.getStatut() + ")";
        }
        if (!chauffeur.permisValide()) {
            return "le permis du chauffeur est expiré";
        }
        Vehicule v = chauffeur.getVehicule();
        if (v == null) {
            return "aucun véhicule n'est associé au chauffeur";
        }
        if (v.getStatut() == StatutVehicule.MAINTENANCE) {
            return "le véhicule est en maintenance";
        }
        if (v.getStatut() != StatutVehicule.DISPONIBLE) {
            return "le véhicule n'est pas disponible (statut " + v.getStatut() + ")";
        }
        if (mission.getType() == TypeMission.LIVRAISON) {
            if (mission.getPoids() != null && v.getCapaciteKg() != null && mission.getPoids() > v.getCapaciteKg()) {
                return "la charge du colis (" + mission.getPoids() + " kg) dépasse la capacité du véhicule ("
                        + v.getCapaciteKg() + " kg)";
            }
        } else if (mission.getNombrePassagers() != null && v.getCapacitePlaces() != null
                && mission.getNombrePassagers() > v.getCapacitePlaces()) {
            return "le véhicule n'a pas assez de places (" + v.getCapacitePlaces() + ")";
        }
        if (missionRepository.existsByChauffeurIdAndStatutIn(chauffeur.getId(), ACTIFS)) {
            return "le chauffeur a déjà une mission en cours";
        }
        return null;
    }

    @Override
    public void verifierEligibilite(Mission mission, Chauffeur chauffeur) {
        String motif = motifIneligibilite(mission, chauffeur);
        if (motif != null) {
            throw new ConflictException("Attribution impossible : " + motif);
        }
    }

    @Override
    public List<CandidatResponse> classer(Mission mission) {
        double coefDistance = parametreService.getDouble(ParametreCles.SCORE_COEF_DISTANCE);
        double coefZone = parametreService.getDouble(ParametreCles.SCORE_COEF_ZONE);
        double coefCharge = parametreService.getDouble(ParametreCles.SCORE_COEF_CHARGE);
        double facteurPriorite = switch (mission.getPriorite()) {
            case NORMALE -> 1.0;
            case URGENTE -> 1.5;
            case TRES_URGENTE -> 2.0;
        };
        LocalDate jour = mission.getDatePrevue().toLocalDate();

        List<CandidatResponse> resultat = new ArrayList<>();
        for (Chauffeur c : chauffeurRepository.findCandidats(StatutChauffeur.DISPONIBLE, StatutVehicule.DISPONIBLE)) {
            if (motifIneligibilite(mission, c) != null) {
                continue;
            }
            Double distance = null;
            if (mission.getLatDepart() != null && mission.getLngDepart() != null
                    && c.getLatitude() != null && c.getLongitude() != null) {
                distance = GeoUtil.haversineKm(c.getLatitude(), c.getLongitude(),
                        mission.getLatDepart(), mission.getLngDepart());
            }
            double scoreDistance = distance == null ? 0.0 : 1.0 / (1.0 + distance);
            boolean memeZone = mission.getZone() != null && c.getZone() != null
                    && mission.getZone().equalsIgnoreCase(c.getZone());
            long missionsDuJour = missionRepository.countByChauffeurIdAndDatePrevueBetween(
                    c.getId(), jour.atStartOfDay(), jour.atTime(LocalTime.MAX));
            double scoreCharge = 1.0 / (1.0 + missionsDuJour);

            double score = coefDistance * facteurPriorite * scoreDistance
                    + coefZone * (memeZone ? 1.0 : 0.0)
                    + coefCharge * scoreCharge;

            resultat.add(new CandidatResponse(c.getId(), c.nomComplet(), c.getVehicule().getImmatriculation(),
                    c.getZone(), distance == null ? null : Math.round(distance * 100.0) / 100.0,
                    missionsDuJour, Math.round(score * 100.0) / 100.0));
        }
        resultat.sort((a, b) -> {
            int cmp = Double.compare(b.score(), a.score());
            return cmp != 0 ? cmp : Long.compare(a.chauffeurId(), b.chauffeurId());
        });
        return resultat;
    }
}
