package com.example.transport.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Preuve de livraison (obligatoire pour cloturer une LIVRAISON, ignoree pour une COURSE). */
@Getter
@Setter
@NoArgsConstructor
public class CompleteRequest {

    @Size(max = 150)
    private String nomReceptionnaire;

    @Size(max = 50)
    private String codeConfirmation;

    @Size(max = 255)
    private String commentaire;
}
