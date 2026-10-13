package com.example.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String immatriculation;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    private Integer annee;

    /** Nombre de places passagers (courses taxi). */
    @Column(name = "capacite_places", nullable = false)
    private Integer capacitePlaces;

    /** Charge maximale en kg (livraisons). */
    @Column(name = "capacite_kg", nullable = false)
    private Double capaciteKg;

    private Double kilometrage = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutVehicule statut = StatutVehicule.DISPONIBLE;
}
