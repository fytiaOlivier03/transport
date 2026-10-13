package com.example.transport.dto.request;

import com.example.transport.entity.StatutChauffeur;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ChauffeurRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 30, message = "Le téléphone ne doit pas dépasser 30 caractères")
    private String telephone;

    @NotBlank(message = "Le numéro de permis est obligatoire")
    @Size(max = 50, message = "Le numéro de permis ne doit pas dépasser 50 caractères")
    private String numeroPermis;

    @NotNull(message = "La date d'expiration du permis est obligatoire")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateExpirationPermis;

    @PastOrPresent(message = "La date d'embauche ne peut pas être dans le futur")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateEmbauche;

    @Size(max = 100, message = "La zone ne doit pas dépasser 100 caractères")
    private String zone;

    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private Double longitude;

    private StatutChauffeur statut;

    private Long vehiculeId;
}
