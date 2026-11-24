package com.example.evaluacion_service.DTO;

import jakarta.validation.constraints.*;

import java.util.List;

public record FinalizarIntentoRequest(
        @NotEmpty List<@NotNull RespuestaRequest> respuestas
) {}