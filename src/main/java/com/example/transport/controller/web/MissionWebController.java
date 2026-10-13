package com.example.transport.controller.web;

import com.example.transport.dto.request.CompleteRequest;
import com.example.transport.dto.request.IncidentRequest;
import com.example.transport.dto.request.MissionRequest;
import com.example.transport.dto.response.MissionResponse;
import com.example.transport.entity.Priorite;
import com.example.transport.entity.StatutMission;
import com.example.transport.entity.TypeIncident;
import com.example.transport.entity.TypeMission;
import com.example.transport.exception.BusinessException;
import com.example.transport.service.ClientService;
import com.example.transport.service.IncidentService;
import com.example.transport.service.MissionService;
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

import java.time.LocalDateTime;

@Controller
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionWebController {

    private final MissionService missionService;
    private final ClientService clientService;
    private final IncidentService incidentService;

    // ---------------------------------------------------------------- liste

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) TypeMission type,
                        @RequestParam(required = false) StatutMission statut,
                        @RequestParam(required = false) Priorite priorite,
                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page", missionService.rechercher(q, type, statut, priorite, null, null, null, pageable));
        model.addAttribute("q", q);
        model.addAttribute("type", type);
        model.addAttribute("statut", statut);
        model.addAttribute("priorite", priorite);
        model.addAttribute("types", TypeMission.values());
        model.addAttribute("statuts", StatutMission.values());
        model.addAttribute("priorites", Priorite.values());
        model.addAttribute("sort", WebUtil.sortValue(pageable));
        model.addAttribute("query", WebUtil.query(pageable, "q", q, "type", type, "statut", statut, "priorite", priorite));
        return "missions/list";
    }

    // ----------------------------------------------------------- formulaire

    @GetMapping("/new")
    public String nouvelle(Model model) {
        MissionRequest r = new MissionRequest();
        r.setDatePrevue(LocalDateTime.now().plusHours(1).withSecond(0).withNano(0));
        model.addAttribute("mission", r);
        return formulaire(model, null);
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("mission") MissionRequest mission, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulaire(model, null);
        }
        try {
            MissionResponse cree = missionService.creer(mission);
            ra.addFlashAttribute("success", "Mission créée (prix calculé : " + cree.prix() + " Ar)");
            return "redirect:/missions/" + cree.id();
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return formulaire(model, null);
        }
    }

    @GetMapping("/{id}/edit")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("mission", missionService.getRequest(id));
        return formulaire(model, id);
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("mission") MissionRequest mission,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulaire(model, id);
        }
        try {
            missionService.modifier(id, mission);
            ra.addFlashAttribute("success", "Mission modifiée");
            return "redirect:/missions/" + id;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return formulaire(model, id);
        }
    }

    private String formulaire(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("clients", clientService.tous());
        model.addAttribute("types", TypeMission.values());
        model.addAttribute("priorites", Priorite.values());
        return "missions/form";
    }

    // --------------------------------------------------------------- detail

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        MissionResponse mission = missionService.trouver(id);
        model.addAttribute("mission", mission);
        model.addAttribute("candidats", missionService.candidats(id));
        model.addAttribute("incidents", incidentService.parMission(id));
        model.addAttribute("typesIncident", TypeIncident.values());
        model.addAttribute("completion", new CompleteRequest());
        model.addAttribute("incidentForm", new IncidentRequest());
        return "missions/detail";
    }

    // -------------------------------------------------------------- actions

    @PostMapping("/{id}/assign-auto")
    public String attribuerAuto(@PathVariable Long id, RedirectAttributes ra) {
        return action(id, ra, "Mission attribuée automatiquement", () -> missionService.attribuerAutomatiquement(id));
    }

    @PostMapping("/{id}/assign")
    public String attribuer(@PathVariable Long id, @RequestParam Long chauffeurId, RedirectAttributes ra) {
        return action(id, ra, "Mission attribuée au chauffeur", () -> missionService.attribuerManuellement(id, chauffeurId));
    }

    @PostMapping("/{id}/pickup")
    public String prendreEnCharge(@PathVariable Long id, RedirectAttributes ra) {
        return action(id, ra, "Prise en charge enregistrée", () -> missionService.prendreEnCharge(id));
    }

    @PostMapping("/{id}/start")
    public String demarrer(@PathVariable Long id, RedirectAttributes ra) {
        return action(id, ra, "Mission démarrée", () -> missionService.demarrer(id));
    }

    @PostMapping("/{id}/complete")
    public String terminer(@PathVariable Long id, @ModelAttribute CompleteRequest completion, RedirectAttributes ra) {
        return action(id, ra, "Mission terminée", () -> missionService.terminer(id, completion));
    }

    @PostMapping("/{id}/cancel")
    public String annuler(@PathVariable Long id, RedirectAttributes ra) {
        return action(id, ra, "Mission annulée", () -> missionService.annuler(id));
    }

    @PostMapping("/{id}/fail")
    public String echec(@PathVariable Long id, @RequestParam TypeIncident type, @RequestParam String description,
                        RedirectAttributes ra) {
        IncidentRequest incident = new IncidentRequest();
        incident.setType(type);
        incident.setDescription(description);
        return action(id, ra, "Échec enregistré", () -> missionService.declarerEchec(id, incident));
    }

    @PostMapping("/{id}/return")
    public String retourner(@PathVariable Long id, RedirectAttributes ra) {
        return action(id, ra, "Colis retourné à l'expéditeur", () -> missionService.retourner(id));
    }

    @PostMapping("/{id}/incidents")
    public String signaler(@PathVariable Long id, @RequestParam TypeIncident type, @RequestParam String description,
                           RedirectAttributes ra) {
        IncidentRequest incident = new IncidentRequest();
        incident.setType(type);
        incident.setDescription(description);
        return action(id, ra, "Incident signalé", () -> incidentService.signaler(id, incident));
    }

    @PostMapping("/incidents/{incidentId}/resolve")
    public String resoudre(@PathVariable Long incidentId, @RequestParam Long missionId,
                           @RequestParam String resolution, RedirectAttributes ra) {
        return action(missionId, ra, "Incident résolu", () -> incidentService.resoudre(incidentId, resolution));
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            missionService.supprimer(id);
            ra.addFlashAttribute("success", "Mission supprimée");
            return "redirect:/missions";
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/missions/" + id;
        }
    }

    private String action(Long id, RedirectAttributes ra, String succes, Runnable action) {
        try {
            action.run();
            ra.addFlashAttribute("success", succes);
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/missions/" + id;
    }
}
