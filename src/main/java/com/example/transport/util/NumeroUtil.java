package com.example.transport.util;

import com.example.transport.entity.TypeMission;

import java.util.UUID;

public final class NumeroUtil {

    private NumeroUtil() {
    }

    public static String generer(TypeMission type) {
        String prefixe = type == TypeMission.COURSE ? "CRS" : "LIV";
        return prefixe + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
