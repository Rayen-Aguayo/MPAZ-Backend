package com.example.evaluacion_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ObservacionRequest(
        @NotNull Long estudianteId,
        @NotNull Long unidadId,
        @NotBlank @Size(max = 500) String texto) {
}
