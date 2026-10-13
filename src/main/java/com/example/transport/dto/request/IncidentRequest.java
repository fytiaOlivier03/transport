package com.example.transport.dto.request;

import com.example.transport.entity.TypeIncident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IncidentRequest {

    @NotNull(message = "Le type d'incident est obligatoire")
    private TypeIncident type;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000)
    private String description;
}
