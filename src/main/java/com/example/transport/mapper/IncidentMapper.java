package com.example.transport.mapper;

import com.example.transport.dto.response.IncidentResponse;
import com.example.transport.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponse toResponse(Incident i) {
        return new IncidentResponse(i.getId(), i.getType(), i.getDateIncident(), i.getDescription(),
                i.getMission().getId(), i.getMission().getNumero(),
                i.getChauffeur() == null ? null : i.getChauffeur().getId(),
                i.getChauffeur() == null ? null : i.getChauffeur().nomComplet(),
                i.getResolution(), i.isResolu());
    }
}
