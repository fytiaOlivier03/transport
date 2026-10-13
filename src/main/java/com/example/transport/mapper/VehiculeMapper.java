package com.example.transport.mapper;

import com.example.transport.dto.request.VehiculeRequest;
import com.example.transport.dto.response.VehiculeResponse;
import com.example.transport.entity.Vehicule;
import org.springframework.stereotype.Component;

@Component
public class VehiculeMapper {

    /** Le statut est gere par le service. */
    public void updateEntity(VehiculeRequest r, Vehicule v) {
        v.setImmatriculation(r.getImmatriculation().trim());
        v.setMarque(r.getMarque().trim());
        v.setModele(r.getModele().trim());
        v.setAnnee(r.getAnnee());
        v.setCapacitePlaces(r.getCapacitePlaces());
        v.setCapaciteKg(r.getCapaciteKg());
        v.setKilometrage(r.getKilometrage() == null ? 0.0 : r.getKilometrage());
    }

    public VehiculeRequest toRequest(Vehicule v) {
        VehiculeRequest r = new VehiculeRequest();
        r.setImmatriculation(v.getImmatriculation());
        r.setMarque(v.getMarque());
        r.setModele(v.getModele());
        r.setAnnee(v.getAnnee());
        r.setCapacitePlaces(v.getCapacitePlaces());
        r.setCapaciteKg(v.getCapaciteKg());
        r.setKilometrage(v.getKilometrage());
        r.setStatut(v.getStatut());
        return r;
    }

    public VehiculeResponse toResponse(Vehicule v) {
        return new VehiculeResponse(v.getId(), v.getImmatriculation(), v.getMarque(), v.getModele(), v.getAnnee(),
                v.getCapacitePlaces(), v.getCapaciteKg(), v.getKilometrage(), v.getStatut());
    }
}
