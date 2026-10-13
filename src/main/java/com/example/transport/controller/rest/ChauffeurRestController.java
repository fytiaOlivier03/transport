package com.example.transport.controller.rest;

import com.example.transport.dto.request.ChauffeurRequest;
import com.example.transport.dto.request.StatutChauffeurRequest;
import com.example.transport.dto.response.ChauffeurResponse;
import com.example.transport.dto.response.PageResponse;
import com.example.transport.entity.StatutChauffeur;
import com.example.transport.service.ChauffeurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chauffeurs")
@RequiredArgsConstructor
public class ChauffeurRestController {

    private final ChauffeurService chauffeurService;

    @PostMapping
    public ResponseEntity<ChauffeurResponse> creer(@Valid @RequestBody ChauffeurRequest request) {
        ChauffeurResponse cree = chauffeurService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cree.id()).toUri();
        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public PageResponse<ChauffeurResponse> lister(@RequestParam(required = false) String q,
                                                  @RequestParam(required = false) StatutChauffeur statut,
                                                  @RequestParam(required = false) String zone,
                                                  @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return PageResponse.of(chauffeurService.rechercher(q, statut, zone, pageable));
    }

    @GetMapping("/available")
    public List<ChauffeurResponse> disponibles() {
        return chauffeurService.disponibles();
    }

    @GetMapping("/{id}")
    public ChauffeurResponse trouver(@PathVariable Long id) {
        return chauffeurService.trouver(id);
    }

    @PutMapping("/{id}")
    public ChauffeurResponse modifier(@PathVariable Long id, @Valid @RequestBody ChauffeurRequest request) {
        return chauffeurService.modifier(id, request);
    }

    @PatchMapping("/{id}/status")
    public ChauffeurResponse changerStatut(@PathVariable Long id, @Valid @RequestBody StatutChauffeurRequest request) {
        return chauffeurService.changerStatut(id, request.statut());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        chauffeurService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
