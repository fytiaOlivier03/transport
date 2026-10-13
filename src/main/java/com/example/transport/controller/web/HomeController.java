package com.example.transport.controller.web;

import com.example.transport.service.ChauffeurService;
import com.example.transport.service.MissionService;
import com.example.transport.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StatistiqueService statistiqueService;
    private final MissionService missionService;
    private final ChauffeurService chauffeurService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", statistiqueService.dashboard());
        model.addAttribute("enAttente", missionService.enAttente());
        model.addAttribute("disponibles", chauffeurService.disponibles());
        return "dashboard";
    }
}
