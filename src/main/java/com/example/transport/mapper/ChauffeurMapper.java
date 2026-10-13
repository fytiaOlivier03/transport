package com.example.transport.mapper;

import com.example.transport.dto.request.ChauffeurRequest;
import com.example.transport.dto.response.ChauffeurResponse;
import com.example.transport.entity.Chauffeur;
import org.springframework.stereotype.Component;

@Component
public class ChauffeurMapper {

    /** Copie les champs simples ; vehicule et statut sont geres par le service. */
    public void updateEntity(ChauffeurRequest r, Chauffeur c) {
        c.setNom(r.getNom().trim());
        c.setPrenom(r.getPrenom().trim());
        c.setTelephone(r.getTelephone().trim());
        c.setNumeroPermis(r.getNumeroPermis().trim());
        c.setDateExpirationPermis(r.getDateExpirationPermis());
        c.setDateEmbauche(r.getDateEmbauche());
        c.setZone(r.getZone() == null || r.getZone().isBlank() ? null : r.getZone().trim());
        c.setLatitude(r.getLatitude());
        c.setLongitude(r.getLongitude());
    }

    public ChauffeurRequest toRequest(Chauffeur c) {
        ChauffeurRequest r = new ChauffeurRequest();
        r.setNom(c.getNom());
        r.setPrenom(c.getPrenom());
        r.setTelephone(c.getTelephone());
        r.setNumeroPermis(c.getNumeroPermis());
        r.setDateExpirationPermis(c.getDateExpirationPermis());
        r.setDateEmbauche(c.getDateEmbauche());
        r.setZone(c.getZone());
        r.setLatitude(c.getLatitude());
        r.setLongitude(c.getLongitude());
        r.setStatut(c.getStatut());
        r.setVehiculeId(c.getVehicule() == null ? null : c.getVehicule().getId());
        return r;
    }

    public ChauffeurResponse toResponse(Chauffeur c) {
        return new ChauffeurResponse(c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(),
                c.getNumeroPermis(), c.getDateExpirationPermis(), c.permisValide(),
                c.getDateEmbauche(), c.getZone(), c.getLatitude(), c.getLongitude(), c.getStatut(),
                c.getVehicule() == null ? null : c.getVehicule().getId(),
                c.getVehicule() == null ? null : c.getVehicule().getImmatriculation());
    }
}
