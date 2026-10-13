package com.example.transport.repository;

import com.example.transport.entity.*;
import com.example.transport.util.RechercheUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Filtres dynamiques sur les missions (recherche, type, statut, periode...). */
public final class MissionSpecs {

    private MissionSpecs() {
    }

    public static Specification<Mission> filtre(String q, TypeMission type, StatutMission statut, Priorite priorite,
                                                Long chauffeurId, String zone, LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                Join<Mission, Client> client = root.join("client", JoinType.LEFT);
                p.add(cb.or(
                        RechercheUtil.like(cb, root.get("numero"), q),
                        RechercheUtil.like(cb, root.get("adresseDepart"), q),
                        RechercheUtil.like(cb, root.get("adresseDestination"), q),
                        RechercheUtil.like(cb, client.get("nom"), q)));
            }
            if (type != null) {
                p.add(cb.equal(root.get("type"), type));
            }
            if (statut != null) {
                p.add(cb.equal(root.get("statut"), statut));
            }
            if (priorite != null) {
                p.add(cb.equal(root.get("priorite"), priorite));
            }
            if (chauffeurId != null) {
                p.add(cb.equal(root.get("chauffeur").get("id"), chauffeurId));
            }
            if (zone != null && !zone.isBlank()) {
                p.add(cb.equal(cb.lower(root.get("zone")), zone.trim().toLowerCase()));
            }
            if (from != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("datePrevue"), from));
            }
            if (to != null) {
                p.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get("datePrevue"), to));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
