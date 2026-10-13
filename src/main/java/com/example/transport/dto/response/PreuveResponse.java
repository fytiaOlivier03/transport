package com.example.transport.dto.response;

import java.time.LocalDateTime;

public record PreuveResponse(LocalDateTime dateHeure, String nomReceptionnaire, String codeConfirmation, String commentaire) {
}
