package com.example.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String prenom;

    @Column(nullable = false)
    private String telephone;

    private String email;

    private String adresse;

    public String nomComplet() {
        return prenom == null || prenom.isBlank() ? nom : prenom + " " + nom;
    }
}
