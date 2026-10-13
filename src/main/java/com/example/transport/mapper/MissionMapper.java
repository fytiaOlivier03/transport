package com.example.transport.mapper;

import com.example.transport.dto.request.MissionRequest;
import com.example.transport.dto.response.MissionResponse;
import com.example.transport.dto.response.PreuveResponse;
import com.example.transport.dto.response.TrackingResponse;
import com.example.transport.entity.Mission;
import com.example.transport.entity.PreuveLivraison;
import org.springframework.stereotype.Component;

@Component
public class MissionMapper {

    /** Copie les champs simples ; client, priorite et prix sont geres par le service. */
    public void updateEntity(MissionRequest r, Mission m) {
        m.setType(r.getType());
        m.setAdresseDepart(r.getAdresseDepart().trim());
        m.setAdresseDestination(r.getAdresseDestination().trim());
        m.setZone(r.getZone() == null || r.getZone().isBlank() ? null : r.getZone().trim());
        m.setLatDepart(r.getLatDepart());
        m.setLngDepart(r.getLngDepart());
        m.setDistanceKm(r.getDistanceKm());
        m.setPoids(r.getPoids());
        m.setVolume(r.getVolume());
        m.setNombrePassagers(r.getNombrePassagers());
        m.setDatePrevue(r.getDatePrevue());
    }

    public MissionRequest toRequest(Mission m) {
        MissionRequest r = new MissionRequest();
        r.setType(m.getType());
        r.setClientId(m.getClient().getId());
        r.setAdresseDepart(m.getAdresseDepart());
        r.setAdresseDestination(m.getAdresseDestination());
        r.setZone(m.getZone());
        r.setLatDepart(m.getLatDepart());
        r.setLngDepart(m.getLngDepart());
        r.setDistanceKm(m.getDistanceKm());
        r.setPoids(m.getPoids());
        r.setVolume(m.getVolume());
        r.setNombrePassagers(m.getNombrePassagers());
        r.setPriorite(m.getPriorite());
        r.setDatePrevue(m.getDatePrevue());
        return r;
    }

    public MissionResponse toResponse(Mission m) {
        return build(m, null);
    }

    public MissionResponse toDetailResponse(Mission m, PreuveLivraison p) {
        PreuveResponse preuve = p == null ? null
                : new PreuveResponse(p.getDateHeure(), p.getNomReceptionnaire(), p.getCodeConfirmation(), p.getCommentaire());
        return build(m, preuve);
    }

    public TrackingResponse toTracking(Mission m) {
        return new TrackingResponse(m.getNumero(), m.getType(), m.getStatut(),
                m.getChauffeur() == null ? null : m.getChauffeur().nomComplet(),
                m.getVehicule() == null ? null : m.getVehicule().getImmatriculation(),
                m.getDateCreation(), m.getDateAssignation(), m.getDateDebut(), m.getDateFin());
    }

    private MissionResponse build(Mission m, PreuveResponse preuve) {
        return new MissionResponse(
                m.getId(), m.getNumero(), m.getType(),
                m.getClient().getId(), m.getClient().nomComplet(),
                m.getAdresseDepart(), m.getAdresseDestination(), m.getZone(),
                m.getDistanceKm(), m.getPoids(), m.getVolume(), m.getNombrePassagers(),
                m.getPriorite(), m.getDatePrevue(),
                m.getPrix(), m.getCommission(), m.getStatut(),
                m.getChauffeur() == null ? null : m.getChauffeur().getId(),
                m.getChauffeur() == null ? null : m.getChauffeur().nomComplet(),
                m.getVehicule() == null ? null : m.getVehicule().getId(),
                m.getVehicule() == null ? null : m.getVehicule().getImmatriculation(),
                m.getDateCreation(), m.getDateAssignation(), m.getDateDebut(), m.getDateFin(),
                preuve);
    }
}
