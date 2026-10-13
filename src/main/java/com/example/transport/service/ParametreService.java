package com.example.transport.service;

import com.example.transport.dto.response.ParametreResponse;

import java.util.List;

public interface ParametreService {
    double getDouble(String cle);

    List<ParametreResponse> lister();

    ParametreResponse modifier(String cle, String valeur);

    void initialiserParDefaut();
}
