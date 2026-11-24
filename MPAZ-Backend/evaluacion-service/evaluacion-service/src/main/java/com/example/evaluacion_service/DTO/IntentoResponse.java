package com.example.evaluacion_service.DTO;

import com.example.evaluacion_service.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record IntentoResponse(
        Long id,
        Long evaluacionId,
        Long estudianteId,
        Integer numeroIntento,
        String estado,
        Integer aciertos,
        Integer totalPreguntas,
        Integer puntajeObtenido,
        BigDecimal nota,
        OffsetDateTime fechaInicio,
        OffsetDateTime fechaTermino
) {

    public static IntentoResponse from(IntentoEvaluacion i) {
        return new IntentoResponse(
                i.getId(),
                i.getEvaluacion().getId(),
                i.getEstudianteId(),
                i.getNumeroIntento(),
                i.getEstado().name(),
                i.getAciertos(),
                i.getTotalPreguntas(),
                i.getPuntajeObtenido(),
                i.getNota(),
                i.getFechaInicio(),
                i.getFechaTermino()
        );
    }
}