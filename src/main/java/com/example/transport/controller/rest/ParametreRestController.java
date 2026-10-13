package com.example.transport.controller.rest;

import com.example.transport.dto.request.ParametreRequest;
import com.example.transport.dto.response.ParametreResponse;
import com.example.transport.service.ParametreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
public class ParametreRestController {

    private final ParametreService parametreService;

    @GetMapping
    public List<ParametreResponse> lister() {
        return parametreService.lister();
    }

    @PutMapping("/{cle}")
    public ParametreResponse modifier(@PathVariable String cle, @Valid @RequestBody ParametreRequest request) {
        return parametreService.modifier(cle, request.valeur());
    }
}
