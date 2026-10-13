package com.example.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Deux chaines de securite :
 *  - /api/**  : API REST, HTTP Basic, sans session ni CSRF
 *  - le reste : interface Thymeleaf, formulaire de connexion + CSRF
 *
 * Roles : ADMIN (configuration), MANAGER (gestion metier), OPERATOR (operations quotidiennes),
 *         USER (consultation).
 */
@Configuration
@EnableWebSecurity
@SuppressWarnings("all")
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // configuration : lecture ADMIN/MANAGER, modification ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/parametres/**").hasRole("ADMIN")
                        .requestMatchers("/api/parametres/**", "/api/statistics/**").hasAnyRole("ADMIN", "MANAGER")
                        // suppressions : gestion metier
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN", "MANAGER")
                        // statut d'un chauffeur / vehicule : operations quotidiennes
                        .requestMatchers(HttpMethod.PATCH, "/api/chauffeurs/*/status", "/api/vehicules/*/status")
                        .hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                        // creation / modification des chauffeurs et vehicules : gestion metier
                        .requestMatchers(HttpMethod.POST, "/api/chauffeurs/**", "/api/vehicules/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/chauffeurs/**", "/api/vehicules/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        // consultation : tous les roles
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                        // clients, missions, incidents : operations quotidiennes
                        .anyRequest().hasAnyRole("ADMIN", "MANAGER", "OPERATOR"));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/error").permitAll()
                        .requestMatchers("/parametres/**").hasRole("ADMIN")
                        .requestMatchers("/statistiques/**").hasAnyRole("ADMIN", "MANAGER")
                        // changement de statut : operations quotidiennes
                        .requestMatchers(HttpMethod.POST, "/chauffeurs/*/status", "/vehicules/*/status")
                        .hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                        // chauffeurs et vehicules : gestion metier
                        .requestMatchers("/chauffeurs/new", "/chauffeurs/*/edit", "/vehicules/new", "/vehicules/*/edit")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/chauffeurs/**", "/vehicules/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        // suppression d'une mission : gestion metier
                        .requestMatchers(HttpMethod.POST, "/missions/*/delete").hasAnyRole("ADMIN", "MANAGER")
                        // clients et missions : operations quotidiennes
                        .requestMatchers("/clients/new", "/clients/*/edit", "/missions/new", "/missions/*/edit")
                        .hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                        .requestMatchers(HttpMethod.POST, "/clients/**", "/missions/**")
                        .hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                        // le reste (consultation) : utilisateur connecte
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}
