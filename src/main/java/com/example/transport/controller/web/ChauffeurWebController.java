package com.example.transport.controller.web;

import com.example.transport.dto.request.ChauffeurRequest;
import com.example.transport.entity.StatutChauffeur;
import com.example.transport.exception.BusinessException;
import com.example.transport.service.ChauffeurService;
import com.example.transport.service.VehiculeService;
import com.example.transport.util.WebUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chauffeurs")
@RequiredArgsConstructor
public class ChauffeurWebController {

    private static final StatutChauffeur[] STATUTS_MANUELS = {
            StatutChauffeur.DISPONIBLE, StatutChauffeur.EN_PAUSE, StatutChauffeur.INDISPONIBLE, StatutChauffeur.SUSPENDU};

    private final ChauffeurService chauffeurService;
    private final VehiculeService vehiculeService;

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) StatutChauffeur statut,
                        @RequestParam(required = false) String zone,
                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page", chauffeurService.rechercher(q, statut, zone, pageable));
        model.addAttribute("q", q);
        model.addAttribute("statut", statut);
        model.addAttribute("zone", zone);
        model.addAttribute("statuts", StatutChauffeur.values());
        model.addAttribute("statutsManuels", STATUTS_MANUELS);
        model.addAttribute("sort", WebUtil.sortValue(pageable));
        model.addAttribute("query", WebUtil.query(pageable, "q", q, "statut", statut, "zone", zone));
        return "chauffeurs/list";
    }

    @GetMapping("/new")
    public String nouveau(Model model) {
        model.addAttribute("chauffeur", new ChauffeurRequest());
        return formulaire(model, null);
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("chauffeur") ChauffeurRequest chauffeur, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulaire(model, null);
        }
        try {
            chauffeurService.creer(chauffeur);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return formulaire(model, null);
        }
        ra.addFlashAttribute("success", "Chauffeur créé avec succès");
        return "redirect:/chauffeurs";
    }

    @GetMapping("/{id}/edit")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("chauffeur", chauffeurService.getRequest(id));
        return formulaire(model, id);
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("chauffeur") ChauffeurRequest chauffeur,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulaire(model, id);
        }
        try {
            chauffeurService.modifier(id, chauffeur);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return formulaire(model, id);
        }
        ra.addFlashAttribute("success", "Chauffeur modifié avec succès");
        return "redirect:/chauffeurs";
    }

    @PostMapping("/{id}/status")
    public String changerStatut(@PathVariable Long id, @RequestParam StatutChauffeur statut, RedirectAttributes ra) {
        try {
            chauffeurService.changerStatut(id, statut);
            ra.addFlashAttribute("success", "Statut mis à jour");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/chauffeurs";
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            chauffeurService.supprimer(id);
            ra.addFlashAttribute("success", "Chauffeur supprimé");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/chauffeurs";
    }

    private String formulaire(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("vehicules", vehiculeService.tous());
        model.addAttribute("statutsManuels", STATUTS_MANUELS);
        return "chauffeurs/form";
    }
}
