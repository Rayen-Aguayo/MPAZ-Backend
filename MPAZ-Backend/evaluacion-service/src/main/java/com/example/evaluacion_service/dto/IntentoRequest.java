package com.example.evaluacion_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record IntentoRequest(@NotNull @Valid List<RespuestaItem> respuestas) {
}
