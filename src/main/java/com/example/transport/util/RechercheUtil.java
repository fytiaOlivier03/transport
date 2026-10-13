package com.example.transport.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public final class RechercheUtil {

    private RechercheUtil() {
    }

    /** Recherche "contient" insensible a la casse. */
    public static Predicate like(CriteriaBuilder cb, Expression<String> expression, String q) {
        return cb.like(cb.lower(expression), "%" + q.trim().toLowerCase() + "%");
    }
}
