package com.example.transport.service.impl;

import com.example.transport.dto.response.ParametreResponse;
import com.example.transport.entity.Parametre;
import com.example.transport.exception.BusinessException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.repository.ParametreRepository;
import com.example.transport.service.ParametreService;
import com.example.transport.util.ParametreCles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class ParametreServiceImpl implements ParametreService {

    private final ParametreRepository parametreRepository;

    @Override
    public double getDouble(String cle) {
        String valeur = parametreRepository.findById(cle)
                .map(Parametre::getValeur)
                .orElseGet(() -> {
                    String[] defaut = ParametreCles.DEFAUTS.get(cle);
                    if (defaut == null) {
                        throw new ResourceNotFoundException("Paramètre inconnu : " + cle);
                    }
                    return defaut[0];
                });
        return Double.parseDouble(valeur.trim().replace(',', '.'));
    }

    @Override
    public List<ParametreResponse> lister() {
        return parametreRepository.findAll().stream()
                .sorted((a, b) -> a.getCle().compareTo(b.getCle()))
                .map(p -> new ParametreResponse(p.getCle(), p.getValeur(), p.getDescription()))
                .toList();
    }

    @Override
    @Transactional
    public ParametreResponse modifier(String cle, String valeur) {
        Parametre p = parametreRepository.findById(cle)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre inconnu : " + cle));
        double nombre;
        try {
            nombre = Double.parseDouble(valeur.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new BusinessException("La valeur du paramètre " + cle + " doit être un nombre");
        }
        if (nombre < 0) {
            throw new BusinessException("La valeur du paramètre " + cle + " ne peut pas être négative");
        }
        p.setValeur(valeur.trim().replace(',', '.'));
        parametreRepository.save(p);
        return new ParametreResponse(p.getCle(), p.getValeur(), p.getDescription());
    }

    @Override
    @Transactional
    public void initialiserParDefaut() {
        ParametreCles.DEFAUTS.forEach((cle, def) -> {
            if (!parametreRepository.existsById(cle)) {
                Parametre p = new Parametre();
                p.setCle(cle);
                p.setValeur(def[0]);
                p.setDescription(def[1]);
                parametreRepository.save(p);
            }
        });
    }
}
