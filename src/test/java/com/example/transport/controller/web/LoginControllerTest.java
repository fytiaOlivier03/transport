package com.example.transport.controller.web;

import com.example.transport.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void registerPageIsAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void registerCreatesUserAndRedirectsToLogin() throws Exception {
        String username = "newuser" + System.currentTimeMillis();
        RequestPostProcessor csrf = Objects.requireNonNull(SecurityMockMvcRequestPostProcessors.csrf());

        mockMvc.perform(post("/register")
                        .with(csrf)
                        .param("username", username)
                        .param("password", "Secret123!")
                        .param("confirmPassword", "Secret123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        org.assertj.core.api.Assertions.assertThat(utilisateurRepository.findByUsername(username)).isPresent();
    }
}
