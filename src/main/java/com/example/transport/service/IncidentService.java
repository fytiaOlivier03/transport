package com.example.transport.service;

import com.example.transport.dto.request.IncidentRequest;
import com.example.transport.dto.response.IncidentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IncidentService {
    IncidentResponse signaler(Long missionId, IncidentRequest request);

    List<IncidentResponse> parMission(Long missionId);

    IncidentResponse resoudre(Long incidentId, String resolution);

    Page<IncidentResponse> lister(Boolean resolu, Pageable pageable);
}
