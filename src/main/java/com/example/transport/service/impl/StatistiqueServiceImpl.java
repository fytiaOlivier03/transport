package com.example.transport.service.impl;

import com.example.transport.dto.response.ActivityStatsResponse;
import com.example.transport.dto.response.DashboardResponse;
import com.example.transport.dto.response.RevenueStatsResponse;
import com.example.transport.entity.*;
import com.example.transport.repository.ChauffeurRepository;
import com.example.transport.repository.MissionRepository;
import com.example.transport.repository.MissionSpecs;
import com.example.transport.repository.VehiculeRepository;
import com.example.transport.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;

/** Les statistiques sont calculees sur la date prevue des missions (filtre de periode). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class StatistiqueServiceImpl implements StatistiqueService {

    private final MissionRepository missionRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final VehiculeRepository vehiculeRepository;

    @Override
    public DashboardResponse dashboard() {
        LocalDate aujourdhui = LocalDate.now();
        List<Mission> terminees = missionRepository.findByStatutAndDateFinBetween(
                StatutMission.TERMINEE, aujourdhui.atStartOfDay(), aujourdhui.atTime(LocalTime.MAX));
        BigDecimal ca = somme(terminees, Mission::getPrix);
        return new DashboardResponse(
                chauffeurRepository.countByStatut(StatutChauffeur.DISPONIBLE), chauffeurRepository.count(),
                vehiculeRepository.countByStatut(StatutVehicule.DISPONIBLE), vehiculeRepository.count(),
                missionRepository.countByStatut(StatutMission.CREEE),
                missionRepository.countByStatutIn(EnumSet.of(
                        StatutMission.ASSIGNEE, StatutMission.PRISE_EN_CHARGE, StatutMission.EN_COURS)),
                terminees.size(), ca);
    }

    @Override
    public RevenueStatsResponse revenus(LocalDate from, LocalDate to, TypeMission type, Long chauffeurId, String zone) {
        LocalDate debut = from == null ? LocalDate.now().minusDays(30) : from;
        LocalDate fin = to == null ? LocalDate.now() : to;
        List<Mission> terminees = charger(debut, fin, type, chauffeurId, zone).stream()
                .filter(m -> m.getStatut() == StatutMission.TERMINEE).toList();

        BigDecimal ca = somme(terminees, Mission::getPrix);
        BigDecimal commission = somme(terminees, Mission::getCommission);
        BigDecimal moyenne = terminees.isEmpty() ? BigDecimal.ZERO
                : ca.divide(BigDecimal.valueOf(terminees.size()), 2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> parChauffeur = new HashMap<>();
        Map<String, BigDecimal> parVehicule = new HashMap<>();
        Map<String, BigDecimal> quotidien = new TreeMap<>();
        Map<String, BigDecimal> mensuel = new TreeMap<>();
        for (Mission m : terminees) {
            BigDecimal prix = m.getPrix() == null ? BigDecimal.ZERO : m.getPrix();
            BigDecimal net = prix.subtract(m.getCommission() == null ? BigDecimal.ZERO : m.getCommission());
            if (m.getChauffeur() != null) {
                parChauffeur.merge(m.getChauffeur().nomComplet(), net, BigDecimal::add);
            }
            if (m.getVehicule() != null) {
                parVehicule.merge(m.getVehicule().getImmatriculation(), prix, BigDecimal::add);
            }
            LocalDate jour = m.getDatePrevue().toLocalDate();
            quotidien.merge(jour.toString(), prix, BigDecimal::add);
            mensuel.merge(YearMonth.from(jour).toString(), prix, BigDecimal::add);
        }
        return new RevenueStatsResponse(debut, fin, ca, commission, ca.subtract(commission), moyenne,
                trierDesc(parChauffeur), trierDesc(parVehicule), quotidien, mensuel);
    }

    @Override
    public ActivityStatsResponse activite(LocalDate from, LocalDate to, TypeMission type, Long chauffeurId, String zone) {
        LocalDate debut = from == null ? LocalDate.now().minusDays(30) : from;
        LocalDate fin = to == null ? LocalDate.now() : to;
        List<Mission> missions = charger(debut, fin, type, chauffeurId, zone);

        long total = missions.size();
        long courses = missions.stream().filter(m -> m.getType() == TypeMission.COURSE).count();
        long terminees = compter(missions, StatutMission.TERMINEE);
        long annulees = compter(missions, StatutMission.ANNULEE);
        long echecs = compter(missions, StatutMission.ECHEC) + compter(missions, StatutMission.RETOURNEE);

        List<Mission> missionsTerminees = missions.stream()
                .filter(m -> m.getStatut() == StatutMission.TERMINEE).toList();
        double distance = missionsTerminees.stream().mapToDouble(Mission::getDistanceKm).sum();

        Double delaiMoyen = missionsTerminees.stream()
                .filter(m -> m.getDateFin() != null && m.getDateCreation() != null)
                .mapToLong(m -> Duration.between(m.getDateCreation(), m.getDateFin()).toMinutes())
                .average().stream().boxed().findFirst().orElse(null);

        Map<String, Long> parChauffeur = compterPar(missionsTerminees,
                m -> m.getChauffeur() == null ? null : m.getChauffeur().nomComplet());
        Map<String, Long> parVehicule = compterPar(missionsTerminees,
                m -> m.getVehicule() == null ? null : m.getVehicule().getImmatriculation());
        Map.Entry<String, Long> topChauffeur = parChauffeur.entrySet().stream().findFirst().orElse(null);
        Map.Entry<String, Long> topVehicule = parVehicule.entrySet().stream().findFirst().orElse(null);

        return new ActivityStatsResponse(debut, fin, total, courses, total - courses,
                terminees, annulees, echecs, arrondi(distance),
                taux(terminees, total), taux(echecs, total), taux(annulees, total),
                delaiMoyen == null ? null : arrondi(delaiMoyen),
                topChauffeur == null ? null : topChauffeur.getKey(), topChauffeur == null ? 0 : topChauffeur.getValue(),
                topVehicule == null ? null : topVehicule.getKey(), topVehicule == null ? 0 : topVehicule.getValue(),
                parChauffeur);
    }

    // ----------------------------------------------------------------- OUTILS

    private List<Mission> charger(LocalDate from, LocalDate to, TypeMission type, Long chauffeurId, String zone) {
        LocalDateTime debut = from.atStartOfDay();
        LocalDateTime fin = to.atTime(LocalTime.MAX);
        return missionRepository.findAll(MissionSpecs.filtre(null, type, null, null, chauffeurId, zone, debut, fin));
    }

    private BigDecimal somme(List<Mission> missions, Function<Mission, BigDecimal> champ) {
        return missions.stream().map(champ).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long compter(List<Mission> missions, StatutMission statut) {
        return missions.stream().filter(m -> m.getStatut() == statut).count();
    }

    /** Compte les missions par cle, tri decroissant. */
    private Map<String, Long> compterPar(List<Mission> missions, Function<Mission, String> cle) {
        Map<String, Long> compteur = new HashMap<>();
        for (Mission m : missions) {
            String k = cle.apply(m);
            if (k != null) {
                compteur.merge(k, 1L, Long::sum);
            }
        }
        Map<String, Long> trie = new LinkedHashMap<>();
        compteur.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> trie.put(e.getKey(), e.getValue()));
        return trie;
    }

    private Map<String, BigDecimal> trierDesc(Map<String, BigDecimal> map) {
        Map<String, BigDecimal> trie = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(e -> trie.put(e.getKey(), e.getValue()));
        return trie;
    }

    private double taux(long partie, long total) {
        return total == 0 ? 0.0 : arrondi(100.0 * partie / total);
    }

    private double arrondi(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
