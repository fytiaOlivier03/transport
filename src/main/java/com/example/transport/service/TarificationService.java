package com.example.transport.service;

import com.example.transport.entity.Priorite;
import com.example.transport.entity.TypeMission;

import java.math.BigDecimal;

public interface TarificationService {
    BigDecimal calculerPrix(TypeMission type, double distanceKm, Double poids, Double volume, Priorite priorite);

    BigDecimal calculerCommission(BigDecimal prix);
}
