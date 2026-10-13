package com.example.transport.service.impl;

import com.example.transport.entity.Priorite;
import com.example.transport.entity.TypeMission;
import com.example.transport.service.ParametreService;
import com.example.transport.service.TarificationService;
import com.example.transport.util.ParametreCles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TarificationServiceImpl implements TarificationService {

    private final ParametreService parametreService;

    @Override
    public BigDecimal calculerPrix(TypeMission type, double distanceKm, Double poids, Double volume, Priorite priorite) {
        double total = parametreService.getDouble(ParametreCles.TARIF_BASE);
        if (type == TypeMission.COURSE) {
            total += distanceKm * parametreService.getDouble(ParametreCles.TARIF_KM_COURSE);
        } else {
            total += distanceKm * parametreService.getDouble(ParametreCles.TARIF_KM_LIVRAISON);
            if (poids != null) {
                total += poids * parametreService.getDouble(ParametreCles.TARIF_KG);
            }
            if (volume != null) {
                total += volume * parametreService.getDouble(ParametreCles.TARIF_M3);
            }
        }
        Priorite p = priorite == null ? Priorite.NORMALE : priorite;
        double majoration = switch (p) {
            case NORMALE -> 0.0;
            case URGENTE -> parametreService.getDouble(ParametreCles.MAJORATION_URGENTE);
            case TRES_URGENTE -> parametreService.getDouble(ParametreCles.MAJORATION_TRES_URGENTE);
        };
        return BigDecimal.valueOf(total * (1 + majoration)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculerCommission(BigDecimal prix) {
        double taux = parametreService.getDouble(ParametreCles.COMMISSION_TAUX);
        return prix.multiply(BigDecimal.valueOf(taux)).setScale(2, RoundingMode.HALF_UP);
    }
}
