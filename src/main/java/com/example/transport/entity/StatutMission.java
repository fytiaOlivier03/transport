package com.example.transport.entity;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Workflow unifie des missions (course taxi ou livraison).
 * CREEE -> ASSIGNEE -> PRISE_EN_CHARGE -> EN_COURS -> TERMINEE
 * Etats alternatifs : ANNULEE, ECHEC, RETOURNEE
 */
public enum StatutMission {
    CREEE, ASSIGNEE, PRISE_EN_CHARGE, EN_COURS, TERMINEE, ANNULEE, ECHEC, RETOURNEE;

    private static final Map<StatutMission, Set<StatutMission>> TRANSITIONS = new EnumMap<>(StatutMission.class);

    static {
        TRANSITIONS.put(CREEE, EnumSet.of(ASSIGNEE, ANNULEE));
        TRANSITIONS.put(ASSIGNEE, EnumSet.of(PRISE_EN_CHARGE, ANNULEE));
        TRANSITIONS.put(PRISE_EN_CHARGE, EnumSet.of(EN_COURS, ANNULEE, ECHEC));
        TRANSITIONS.put(EN_COURS, EnumSet.of(TERMINEE, ECHEC));
        TRANSITIONS.put(ECHEC, EnumSet.of(RETOURNEE));
        TRANSITIONS.put(TERMINEE, EnumSet.noneOf(StatutMission.class));
        TRANSITIONS.put(ANNULEE, EnumSet.noneOf(StatutMission.class));
        TRANSITIONS.put(RETOURNEE, EnumSet.noneOf(StatutMission.class));
    }

    public boolean peutPasserA(StatutMission cible) {
        return TRANSITIONS.get(this).contains(cible);
    }

    public boolean estActif() {
        return this == ASSIGNEE || this == PRISE_EN_CHARGE || this == EN_COURS;
    }
}
