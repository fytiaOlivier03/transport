package com.example.transport.controller.rest;

import com.example.transport.dto.request.ResolutionRequest;
import com.example.transport.dto.response.IncidentResponse;
import com.example.transport.dto.response.PageResponse;
import com.example.transport.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentRestController {

    private final IncidentService incidentService;

    @GetMapping
    public PageResponse<IncidentResponse> lister(@RequestParam(required = false) Boolean resolu,
                                                 @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return PageResponse.of(incidentService.lister(resolu, pageable));
    }

    @PutMapping("/{id}/resolve")
    public IncidentResponse resoudre(@PathVariable Long id, @Valid @RequestBody ResolutionRequest request) {
        return incidentService.resoudre(id, request.resolution());
    }
}
