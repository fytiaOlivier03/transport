package com.example.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Parametre configurable par l'administrateur (tarifs, commission, coefficients du score...). */
@Entity
@Table(name = "parametre")
@Getter
@Setter
@NoArgsConstructor
public class Parametre {

    @Id
    @Column(name = "cle")
    private String cle;

    @Column(name = "valeur", nullable = false)
    private String valeur;

    private String description;
}
