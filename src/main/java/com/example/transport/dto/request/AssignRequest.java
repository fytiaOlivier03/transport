package com.example.transport.dto.request;

/** chauffeurId absent = attribution automatique (meilleur score). */
public record AssignRequest(Long chauffeurId) {
}
