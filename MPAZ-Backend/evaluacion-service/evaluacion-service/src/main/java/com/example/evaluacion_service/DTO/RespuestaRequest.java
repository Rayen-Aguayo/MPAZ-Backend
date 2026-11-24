package com.example.evaluacion_service.DTO;

import jakarta.validation.constraints.*;

public record RespuestaRequest(
        @NotNull @Positive Long preguntaId,
        @NotNull @Min(0) @Max(3) Integer elegida
) {}