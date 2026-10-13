package com.example.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Un chauffeur (taxi) ou livreur : meme entite pour les deux metiers. */
@Entity
@Table(name = "chauffeur")
@Getter
@Setter
@NoArgsConstructor
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String telephone;

    @Column(name = "numero_permis", nullable = false, unique = true)
    private String numeroPermis;

    @Column(name = "date_expiration_permis", nullable = false)
    private LocalDate dateExpirationPermis;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    /** Zone d'activite (ex : Centre, Nord, Sud). */
    private String zone;

    /** Derniere position connue (optionnelle, utilisee pour le score d'attribution). */
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutChauffeur statut = StatutChauffeur.DISPONIBLE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", unique = true)
    private Vehicule vehicule;

    public boolean permisValide() {
        return dateExpirationPermis != null && !dateExpirationPermis.isBefore(LocalDate.now());
    }

    public String nomComplet() {
        return prenom + " " + nom;
    }
}
