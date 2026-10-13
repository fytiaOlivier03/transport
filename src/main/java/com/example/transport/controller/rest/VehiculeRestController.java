package com.example.transport.controller.rest;

import com.example.transport.dto.request.StatutVehiculeRequest;
import com.example.transport.dto.request.VehiculeRequest;
import com.example.transport.dto.response.PageResponse;
import com.example.transport.dto.response.VehiculeResponse;
import com.example.transport.entity.StatutVehicule;
import com.example.transport.service.VehiculeService;
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
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeRestController {

    private final VehiculeService vehiculeService;

    @PostMapping
    public ResponseEntity<VehiculeResponse> creer(@Valid @RequestBody VehiculeRequest request) {
        VehiculeResponse cree = vehiculeService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cree.id()).toUri();
        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public PageResponse<VehiculeResponse> lister(@RequestParam(required = false) String q,
                                                 @RequestParam(required = false) StatutVehicule statut,
                                                 @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return PageResponse.of(vehiculeService.rechercher(q, statut, pageable));
    }

    @GetMapping("/available")
    public List<VehiculeResponse> disponibles() {
        return vehiculeService.disponibles();
    }

    @GetMapping("/{id}")
    public VehiculeResponse trouver(@PathVariable Long id) {
        return vehiculeService.trouver(id);
    }

    @PutMapping("/{id}")
    public VehiculeResponse modifier(@PathVariable Long id, @Valid @RequestBody VehiculeRequest request) {
        return vehiculeService.modifier(id, request);
    }

    @PatchMapping("/{id}/status")
    public VehiculeResponse changerStatut(@PathVariable Long id, @Valid @RequestBody StatutVehiculeRequest request) {
        return vehiculeService.changerStatut(id, request.statut());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        vehiculeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
