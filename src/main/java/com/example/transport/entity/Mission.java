package com.example.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Mission = course de taxi (COURSE) ou livraison de colis (LIVRAISON). */
@Entity
@Table(name = "mission")
@Getter
@Setter
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mission", nullable = false)
    private TypeMission type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "adresse_depart", nullable = false)
    private String adresseDepart;

    @Column(name = "adresse_destination", nullable = false)
    private String adresseDestination;

    /** Zone de depart / d'enlevement. */
    private String zone;

    @Column(name = "lat_depart")
    private Double latDepart;

    @Column(name = "lng_depart")
    private Double lngDepart;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    /** Livraison uniquement. */
    private Double poids;
    private Double volume;

    /** Course uniquement. */
    @Column(name = "nombre_passagers")
    private Integer nombrePassagers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite = Priorite.NORMALE;

    @Column(name = "date_prevue", nullable = false)
    private LocalDateTime datePrevue;

    @Column(precision = 12, scale = 2)
    private BigDecimal prix;

    @Column(precision = 12, scale = 2)
    private BigDecimal commission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMission statut = StatutMission.CREEE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_id")
    private Chauffeur chauffeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_assignation")
    private LocalDateTime dateAssignation;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;
}
