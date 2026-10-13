package com.example.transport.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record RevenueStatsResponse(
        LocalDate from, LocalDate to,
        BigDecimal chiffreAffaires, BigDecimal commissionTotale, BigDecimal revenuNetChauffeurs,
        BigDecimal revenuMoyenParMission,
        Map<String, BigDecimal> revenuParChauffeur,
        Map<String, BigDecimal> revenuParVehicule,
        Map<String, BigDecimal> revenuQuotidien,
        Map<String, BigDecimal> revenuMensuel) {
}
