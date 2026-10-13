package com.example.transport.util;

import org.springframework.data.domain.Pageable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class WebUtil {

    private WebUtil() {
    }

    /** Valeur "propriete,direction" du tri courant (par defaut id,desc). */
    public static String sortValue(Pageable pageable) {
        return pageable.getSort().stream().findFirst()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .orElse("id,desc");
    }

    /**
     * Construit la partie "filtres" de l'URL de pagination (se termine par '&').
     * Usage : query(pageable, "q", q, "statut", statut)
     */
    public static String query(Pageable pageable, Object... clesValeurs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < clesValeurs.length; i += 2) {
            Object valeur = clesValeurs[i + 1];
            if (valeur != null && !valeur.toString().isBlank()) {
                sb.append(clesValeurs[i]).append('=')
                        .append(URLEncoder.encode(valeur.toString(), StandardCharsets.UTF_8)).append('&');
            }
        }
        sb.append("size=").append(pageable.getPageSize()).append('&');
        sb.append("sort=").append(sortValue(pageable)).append('&');
        return sb.toString();
    }
}
