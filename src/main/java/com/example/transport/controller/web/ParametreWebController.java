package com.example.transport.controller.web;

import com.example.transport.exception.BusinessException;
import com.example.transport.service.ParametreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parametres")
@RequiredArgsConstructor
public class ParametreWebController {

    private final ParametreService parametreService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("parametres", parametreService.lister());
        return "parametres";
    }

    @PostMapping
    public String modifier(@RequestParam String cle, @RequestParam String valeur, RedirectAttributes ra) {
        try {
            parametreService.modifier(cle, valeur);
            ra.addFlashAttribute("success", "Paramètre « " + cle + " » mis à jour");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/parametres";
    }
}
