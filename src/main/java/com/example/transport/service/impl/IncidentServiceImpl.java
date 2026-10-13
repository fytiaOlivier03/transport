package com.example.transport.service.impl;

import com.example.transport.dto.request.IncidentRequest;
import com.example.transport.dto.response.IncidentResponse;
import com.example.transport.entity.Incident;
import com.example.transport.entity.Mission;
import com.example.transport.entity.StatutMission;
import com.example.transport.exception.ConflictException;
import com.example.transport.exception.InvalidOperationException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.IncidentMapper;
import com.example.transport.repository.IncidentRepository;
import com.example.transport.repository.MissionRepository;
import com.example.transport.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final MissionRepository missionRepository;
    private final IncidentMapper mapper;

    @Override
    @Transactional
    public IncidentResponse signaler(Long missionId, IncidentRequest request) {
        Mission m = missionRepository.findById(missionId)
                .orElseThrow(() -> ResourceNotFoundException.of("Mission", missionId));
        if (!m.getStatut().estActif() && m.getStatut() != StatutMission.ECHEC) {
            throw new InvalidOperationException(
                    "Un incident ne peut être signalé que sur une mission en cours de traitement");
        }
        Incident i = new Incident();
        i.setType(request.getType());
        i.setDescription(request.getDescription());
        i.setDateIncident(LocalDateTime.now());
        i.setMission(m);
        i.setChauffeur(m.getChauffeur());
        return mapper.toResponse(incidentRepository.save(i));
    }

    @Override
    public List<IncidentResponse> parMission(Long missionId) {
        if (!missionRepository.existsById(missionId)) {
            throw ResourceNotFoundException.of("Mission", missionId);
        }
        return incidentRepository.findByMissionIdOrderByDateIncidentDesc(missionId).stream()
                .map(i -> mapper.toResponse(i)).toList();
    }

    @Override
    @Transactional
    public IncidentResponse resoudre(Long incidentId, String resolution) {
        Incident i = incidentRepository.findById(incidentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Incident", incidentId));
        if (i.isResolu()) {
            throw new ConflictException("Cet incident est déjà résolu");
        }
        i.setResolution(resolution);
        i.setResolu(true);
        return mapper.toResponse(incidentRepository.save(i));
    }

    @Override
    public Page<IncidentResponse> lister(Boolean resolu, Pageable pageable) {
        Page<Incident> page = resolu == null
                ? incidentRepository.findAll(pageable)
                : incidentRepository.findByResolu(resolu, pageable);
        return page.map(i -> mapper.toResponse(i));
    }
}
