package com.example.transport.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @Size(max = 100)
    private String prenom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 30)
    private String telephone;

    @Email(message = "Adresse email invalide")
    @Size(max = 150)
    private String email;

    @Size(max = 255)
    private String adresse;
}
