package com.example.transport.service;

import com.example.transport.dto.response.ActivityStatsResponse;
import com.example.transport.dto.response.DashboardResponse;
import com.example.transport.dto.response.RevenueStatsResponse;
import com.example.transport.entity.TypeMission;

import java.time.LocalDate;

public interface StatistiqueService {
    DashboardResponse dashboard();

    RevenueStatsResponse revenus(LocalDate from, LocalDate to, TypeMission type, Long chauffeurId, String zone);

    ActivityStatsResponse activite(LocalDate from, LocalDate to, TypeMission type, Long chauffeurId, String zone);
}
