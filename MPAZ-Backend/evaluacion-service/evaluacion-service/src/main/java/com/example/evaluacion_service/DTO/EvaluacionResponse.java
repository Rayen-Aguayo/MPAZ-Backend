package com.example.evaluacion_service.DTO;

import com.example.evaluacion_service.model.Evaluacion;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record EvaluacionResponse(
        Long id,
        Long unidadId,
        Long asignacionId,
        String titulo,
        BigDecimal porcentajeExigencia,
        Integer puntajeMaximo,
        String estado,
        OffsetDateTime creadaEn
) {

    public static EvaluacionResponse from(Evaluacion e) {
        return new EvaluacionResponse(
                e.getId(),
                e.getUnidadId(),
                e.getAsignacionId(),
                e.getTitulo(),
                e.getPorcentajeExigencia(),
                e.getPuntajeMaximo(),
                e.getEstado().name(),
                e.getCreadaEn()
        );
    }
}