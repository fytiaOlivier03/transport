package com.example.transport.util;

import java.util.LinkedHashMap;
import java.util.Map;

/** Cles des parametres configurables et leurs valeurs par defaut (montants en Ariary). */
public final class ParametreCles {

    public static final String TARIF_BASE = "tarif.base";
    public static final String TARIF_KM_COURSE = "tarif.km.course";
    public static final String TARIF_KM_LIVRAISON = "tarif.km.livraison";
    public static final String TARIF_KG = "tarif.kg";
    public static final String TARIF_M3 = "tarif.m3";
    public static final String MAJORATION_URGENTE = "majoration.urgente";
    public static final String MAJORATION_TRES_URGENTE = "majoration.tres_urgente";
    public static final String COMMISSION_TAUX = "commission.taux";
    public static final String SCORE_COEF_DISTANCE = "score.coef.distance";
    public static final String SCORE_COEF_ZONE = "score.coef.zone";
    public static final String SCORE_COEF_CHARGE = "score.coef.charge";

    /** cle -> {valeur par defaut, description} */
    public static final Map<String, String[]> DEFAUTS = new LinkedHashMap<>();

    static {
        DEFAUTS.put(TARIF_BASE, new String[]{"2000", "Frais fixes de prise en charge (Ar)"});
        DEFAUTS.put(TARIF_KM_COURSE, new String[]{"1500", "Prix du km pour une course taxi (Ar)"});
        DEFAUTS.put(TARIF_KM_LIVRAISON, new String[]{"1000", "Prix du km pour une livraison (Ar)"});
        DEFAUTS.put(TARIF_KG, new String[]{"200", "Supplement par kg de colis (Ar)"});
        DEFAUTS.put(TARIF_M3, new String[]{"5000", "Supplement par m3 de colis (Ar)"});
        DEFAUTS.put(MAJORATION_URGENTE, new String[]{"0.20", "Majoration priorite URGENTE (0.20 = +20 %)"});
        DEFAUTS.put(MAJORATION_TRES_URGENTE, new String[]{"0.50", "Majoration priorite TRES_URGENTE (0.50 = +50 %)"});
        DEFAUTS.put(COMMISSION_TAUX, new String[]{"0.15", "Taux de commission de la societe (0.15 = 15 %)"});
        DEFAUTS.put(SCORE_COEF_DISTANCE, new String[]{"50", "Coefficient du score : proximite du chauffeur"});
        DEFAUTS.put(SCORE_COEF_ZONE, new String[]{"30", "Coefficient du score : meme zone"});
        DEFAUTS.put(SCORE_COEF_CHARGE, new String[]{"20", "Coefficient du score : equite de charge (moins de missions = mieux)"});
    }

    private ParametreCles() {
    }
}
