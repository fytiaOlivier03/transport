package com.example.transport.controller.web;

import com.example.transport.dto.request.VehiculeRequest;
import com.example.transport.entity.StatutVehicule;
import com.example.transport.exception.BusinessException;
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
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeWebController {

    private static final StatutVehicule[] STATUTS_MANUELS = {
            StatutVehicule.DISPONIBLE, StatutVehicule.MAINTENANCE, StatutVehicule.HORS_SERVICE};

    private final VehiculeService vehiculeService;

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) StatutVehicule statut,
                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page", vehiculeService.rechercher(q, statut, pageable));
        model.addAttribute("q", q);
        model.addAttribute("statut", statut);
        model.addAttribute("statuts", StatutVehicule.values());
        model.addAttribute("statutsManuels", STATUTS_MANUELS);
        model.addAttribute("sort", WebUtil.sortValue(pageable));
        model.addAttribute("query", WebUtil.query(pageable, "q", q, "statut", statut));
        return "vehicules/list";
    }

    @GetMapping("/new")
    public String nouveau(Model model) {
        model.addAttribute("vehicule", new VehiculeRequest());
        return formulaire(model, null);
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("vehicule") VehiculeRequest vehicule, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulaire(model, null);
        }
        try {
            vehiculeService.creer(vehicule);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return formulaire(model, null);
        }
        ra.addFlashAttribute("success", "Véhicule créé avec succès");
        return "redirect:/vehicules";
    }

    @GetMapping("/{id}/edit")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("vehicule", vehiculeService.getRequest(id));
        return formulaire(model, id);
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("vehicule") VehiculeRequest vehicule,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulaire(model, id);
        }
        try {
            vehiculeService.modifier(id, vehicule);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return formulaire(model, id);
        }
        ra.addFlashAttribute("success", "Véhicule modifié avec succès");
        return "redirect:/vehicules";
    }

    @PostMapping("/{id}/status")
    public String changerStatut(@PathVariable Long id, @RequestParam StatutVehicule statut, RedirectAttributes ra) {
        try {
            vehiculeService.changerStatut(id, statut);
            ra.addFlashAttribute("success", "Statut mis à jour");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vehicules";
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            vehiculeService.supprimer(id);
            ra.addFlashAttribute("success", "Véhicule supprimé");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vehicules";
    }

    private String formulaire(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("statutsManuels", STATUTS_MANUELS);
        return "vehicules/form";
    }
}
