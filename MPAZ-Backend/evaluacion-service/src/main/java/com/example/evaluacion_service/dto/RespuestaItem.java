package com.example.evaluacion_service.dto;

import jakarta.validation.constraints.NotNull;

public record RespuestaItem(@NotNull Long preguntaId, Integer elegida) {
}
