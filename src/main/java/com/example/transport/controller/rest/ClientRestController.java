package com.example.transport.controller.rest;

import com.example.transport.dto.request.ClientRequest;
import com.example.transport.dto.response.ClientResponse;
import com.example.transport.dto.response.PageResponse;
import com.example.transport.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientRestController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> creer(@Valid @RequestBody ClientRequest request) {
        ClientResponse cree = clientService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cree.id()).toUri();
        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public PageResponse<ClientResponse> lister(@RequestParam(required = false) String q,
                                               @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return PageResponse.of(clientService.rechercher(q, pageable));
    }

    @GetMapping("/{id}")
    public ClientResponse trouver(@PathVariable Long id) {
        return clientService.trouver(id);
    }

    @PutMapping("/{id}")
    public ClientResponse modifier(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return clientService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        clientService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
