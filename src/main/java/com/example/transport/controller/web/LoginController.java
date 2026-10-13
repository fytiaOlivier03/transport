package com.example.transport.controller.web;

import com.example.transport.entity.Role;
import com.example.transport.entity.Utilisateur;
import com.example.transport.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           RedirectAttributes redirectAttributes) {
        String nom = username == null ? "" : username.trim();
        if (nom.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Le nom d'utilisateur et le mot de passe sont obligatoires.");
            return "redirect:/register?error";
        }
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les mots de passe ne correspondent pas.");
            return "redirect:/register?error";
        }
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Le mot de passe doit contenir au moins 6 caractères.");
            return "redirect:/register?error";
        }
        if (utilisateurRepository.findByUsername(nom).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Ce nom d'utilisateur existe déjà.");
            return "redirect:/register?error";
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(nom);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setRole(Role.USER);
        utilisateur.setEnabled(true);
        utilisateurRepository.save(utilisateur);

        return "redirect:/login?registered";
    }
}
