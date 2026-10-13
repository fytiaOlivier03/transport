package com.example.transport.mapper;

import com.example.transport.dto.request.ClientRequest;
import com.example.transport.dto.response.ClientResponse;
import com.example.transport.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public void updateEntity(ClientRequest r, Client c) {
        c.setNom(r.getNom().trim());
        c.setPrenom(r.getPrenom() == null || r.getPrenom().isBlank() ? null : r.getPrenom().trim());
        c.setTelephone(r.getTelephone().trim());
        c.setEmail(r.getEmail() == null || r.getEmail().isBlank() ? null : r.getEmail().trim());
        c.setAdresse(r.getAdresse() == null || r.getAdresse().isBlank() ? null : r.getAdresse().trim());
    }

    public ClientRequest toRequest(Client c) {
        ClientRequest r = new ClientRequest();
        r.setNom(c.getNom());
        r.setPrenom(c.getPrenom());
        r.setTelephone(c.getTelephone());
        r.setEmail(c.getEmail());
        r.setAdresse(c.getAdresse());
        return r;
    }

    public ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getEmail(), c.getAdresse());
    }
}
