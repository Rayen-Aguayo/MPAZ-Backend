package com.example.asignacion_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AsignacionRequest(
        @NotNull Long unidadId,
        @NotBlank String curso,
        @NotNull Long fechaInicio,
        @NotNull Long fechaFin,
        @NotNull @Min(1) Integer maxIntentos,
        Boolean notificarCurso,
        Boolean permitirVerRespuestas) {
}
