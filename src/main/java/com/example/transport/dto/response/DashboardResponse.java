package com.example.transport.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(
        long chauffeursDisponibles, long chauffeursTotal,
        long vehiculesDisponibles, long vehiculesTotal,
        long missionsEnAttente, long missionsEnCours,
        long missionsTermineesAujourdhui, BigDecimal chiffreAffairesAujourdhui) {
}
