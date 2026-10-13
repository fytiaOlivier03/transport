package com.example.transport.controller.web;

import com.example.transport.dto.request.ClientRequest;
import com.example.transport.exception.BusinessException;
import com.example.transport.service.ClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientWebController {

    private final ClientService clientService;

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page", clientService.rechercher(q, pageable));
        model.addAttribute("q", q);
        model.addAttribute("sort", WebUtil.sortValue(pageable));
        model.addAttribute("query", WebUtil.query(pageable, "q", q));
        return "clients/list";
    }

    @GetMapping("/new")
    public String nouveau(Model model) {
        model.addAttribute("client", new ClientRequest());
        model.addAttribute("id", null);
        return "clients/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("client") ClientRequest client, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("id", null);
            return "clients/form";
        }
        clientService.creer(client);
        ra.addFlashAttribute("success", "Client créé avec succès");
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.getRequest(id));
        model.addAttribute("id", id);
        return "clients/form";
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("client") ClientRequest client,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "clients/form";
        }
        clientService.modifier(id, client);
        ra.addFlashAttribute("success", "Client modifié avec succès");
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            clientService.supprimer(id);
            ra.addFlashAttribute("success", "Client supprimé");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clients";
    }
}
