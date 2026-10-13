package com.example.transport.dto.response;

import java.time.LocalDate;
import java.util.Map;

public record ActivityStatsResponse(
        LocalDate from, LocalDate to,
        long nombreMissions, long nombreCourses, long nombreLivraisons,
        long terminees, long annulees, long echecs,
        double distanceTotaleKm,
        double tauxReussite, double tauxEchec, double tauxAnnulation,
        Double delaiMoyenMinutes,
        String chauffeurLePlusActif, long missionsChauffeurLePlusActif,
        String vehiculeLePlusUtilise, long missionsVehiculeLePlusUtilise,
        Map<String, Long> missionsParChauffeur) {
}
