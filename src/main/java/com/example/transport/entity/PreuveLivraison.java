package com.example.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "preuve_livraison")
@Getter
@Setter
@NoArgsConstructor
public class PreuveLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", unique = true)
    private Mission mission;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "nom_receptionnaire", nullable = false)
    private String nomReceptionnaire;

    @Column(name = "code_confirmation", nullable = false)
    private String codeConfirmation;

    private String commentaire;
}
