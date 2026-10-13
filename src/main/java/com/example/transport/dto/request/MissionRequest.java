package com.example.transport.dto.request;

import com.example.transport.entity.Priorite;
import com.example.transport.entity.TypeMission;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MissionRequest {

    @NotNull(message = "Le type de mission est obligatoire (COURSE ou LIVRAISON)")
    private TypeMission type;

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotBlank(message = "L'adresse de départ / d'enlèvement est obligatoire")
    @Size(max = 255)
    private String adresseDepart;

    @NotBlank(message = "L'adresse de destination / de livraison est obligatoire")
    @Size(max = 255)
    private String adresseDestination;

    @Size(max = 100)
    private String zone;

    @DecimalMin(value = "-90.0", message = "Latitude invalide")
    @DecimalMax(value = "90.0", message = "Latitude invalide")
    private Double latDepart;

    @DecimalMin(value = "-180.0", message = "Longitude invalide")
    @DecimalMax(value = "180.0", message = "Longitude invalide")
    private Double lngDepart;

    @NotNull(message = "La distance est obligatoire")
    @Positive(message = "La distance doit être positive")
    private Double distanceKm;

    /** Livraison : poids en kg. */
    @Positive(message = "Le poids doit être positif")
    private Double poids;

    /** Livraison : volume en m3. */
    @Positive(message = "Le volume doit être positif")
    private Double volume;

    /** Course : nombre de passagers. */
    @Positive(message = "Le nombre de passagers doit être positif")
    private Integer nombrePassagers;

    private Priorite priorite = Priorite.NORMALE;

    @NotNull(message = "La date et l'heure prévues sont obligatoires")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime datePrevue;
}
