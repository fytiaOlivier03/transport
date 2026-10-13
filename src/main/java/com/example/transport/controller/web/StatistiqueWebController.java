package com.example.transport.controller.web;

import com.example.transport.entity.TypeMission;
import com.example.transport.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/statistiques")
@RequiredArgsConstructor
public class StatistiqueWebController {

    private final StatistiqueService statistiqueService;

    @GetMapping
    public String statistiques(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) TypeMission type,
            Model model) {
        LocalDate debut = from == null ? LocalDate.now().minusDays(30) : from;
        LocalDate fin = to == null ? LocalDate.now() : to;
        model.addAttribute("from", debut);
        model.addAttribute("to", fin);
        model.addAttribute("type", type);
        model.addAttribute("types", TypeMission.values());
        model.addAttribute("revenu", statistiqueService.revenus(debut, fin, type, null, null));
        model.addAttribute("activite", statistiqueService.activite(debut, fin, type, null, null));
        return "statistiques";
    }
}
