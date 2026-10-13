package com.example.transport.controller.rest;

import com.example.transport.dto.response.ActivityStatsResponse;
import com.example.transport.dto.response.DashboardResponse;
import com.example.transport.dto.response.RevenueStatsResponse;
import com.example.transport.entity.TypeMission;
import com.example.transport.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatistiqueRestController {

    private final StatistiqueService statistiqueService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return statistiqueService.dashboard();
    }

    /** Par defaut : les 30 derniers jours. */
    @GetMapping("/revenue")
    public RevenueStatsResponse revenus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) TypeMission type,
            @RequestParam(required = false) Long chauffeurId,
            @RequestParam(required = false) String zone) {
        return statistiqueService.revenus(from, to, type, chauffeurId, zone);
    }

    @GetMapping("/activity")
    public ActivityStatsResponse activite(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) TypeMission type,
            @RequestParam(required = false) Long chauffeurId,
            @RequestParam(required = false) String zone) {
        return statistiqueService.activite(from, to, type, chauffeurId, zone);
    }
}
