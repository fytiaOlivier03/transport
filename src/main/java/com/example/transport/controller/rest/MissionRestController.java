package com.example.transport.controller.rest;

import com.example.transport.dto.request.AssignRequest;
import com.example.transport.dto.request.CompleteRequest;
import com.example.transport.dto.request.IncidentRequest;
import com.example.transport.dto.request.MissionRequest;
import com.example.transport.dto.response.*;
import com.example.transport.entity.Priorite;
import com.example.transport.entity.StatutMission;
import com.example.transport.entity.TypeMission;
import com.example.transport.service.IncidentService;
import com.example.transport.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/** Missions = courses taxi (COURSE) et livraisons (LIVRAISON). */
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionRestController {

    private final MissionService missionService;
    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<MissionResponse> creer(@Valid @RequestBody MissionRequest request) {
        MissionResponse cree = missionService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cree.id()).toUri();
        return ResponseEntity.created(location).body(cree);
    }

    /** Ex : GET /api/missions?page=0&size=10&sort=datePrevue,desc&statut=EN_COURS&type=LIVRAISON */
    @GetMapping
    public PageResponse<MissionResponse> lister(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) TypeMission type,
            @RequestParam(required = false) StatutMission statut,
            @RequestParam(required = false) Priorite priorite,
            @RequestParam(required = false) Long chauffeurId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return PageResponse.of(missionService.rechercher(q, type, statut, priorite, chauffeurId, from, to, pageable));
    }

    @GetMapping("/pending")
    public List<MissionResponse> enAttente() {
        return missionService.enAttente();
    }

    @GetMapping("/{id}")
    public MissionResponse trouver(@PathVariable Long id) {
        return missionService.trouver(id);
    }

    @PutMapping("/{id}")
    public MissionResponse modifier(@PathVariable Long id, @Valid @RequestBody MissionRequest request) {
        return missionService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        missionService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tracking")
    public TrackingResponse suivi(@PathVariable Long id) {
        return missionService.suivi(id);
    }

    @GetMapping("/{id}/candidates")
    public List<CandidatResponse> candidats(@PathVariable Long id) {
        return missionService.candidats(id);
    }

    /** Corps vide = attribution automatique ; {"chauffeurId": 3} = attribution manuelle. */
    @PostMapping("/{id}/assign")
    public MissionResponse attribuer(@PathVariable Long id, @RequestBody(required = false) AssignRequest request) {
        if (request == null || request.chauffeurId() == null) {
            return missionService.attribuerAutomatiquement(id);
        }
        return missionService.attribuerManuellement(id, request.chauffeurId());
    }

    @PostMapping("/{id}/pickup")
    public MissionResponse prendreEnCharge(@PathVariable Long id) {
        return missionService.prendreEnCharge(id);
    }

    @PostMapping("/{id}/start")
    public MissionResponse demarrer(@PathVariable Long id) {
        return missionService.demarrer(id);
    }

    /** Livraison : corps obligatoire (nomReceptionnaire + codeConfirmation). Course : corps ignore. */
    @PostMapping("/{id}/complete")
    public MissionResponse terminer(@PathVariable Long id, @Valid @RequestBody(required = false) CompleteRequest preuve) {
        return missionService.terminer(id, preuve);
    }

    @PostMapping("/{id}/cancel")
    public MissionResponse annuler(@PathVariable Long id) {
        return missionService.annuler(id);
    }

    @PostMapping("/{id}/fail")
    public MissionResponse echec(@PathVariable Long id, @Valid @RequestBody IncidentRequest request) {
        return missionService.declarerEchec(id, request);
    }

    @PostMapping("/{id}/return")
    public MissionResponse retourner(@PathVariable Long id) {
        return missionService.retourner(id);
    }

    @PostMapping("/{id}/incidents")
    public ResponseEntity<IncidentResponse> signaler(@PathVariable Long id, @Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.status(201).body(incidentService.signaler(id, request));
    }

    @GetMapping("/{id}/incidents")
    public List<IncidentResponse> incidents(@PathVariable Long id) {
        return incidentService.parMission(id);
    }
}
