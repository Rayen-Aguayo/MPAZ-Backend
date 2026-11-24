package com.example.evaluacion_service.DTO;

import com.example.evaluacion_service.model.ObservacionPedagogica;

import java.time.OffsetDateTime;

public record ObservacionResponse(
        Long id,
        Long estudianteId,
        Long unidadId,
        Long docenteId,
        String texto,
        OffsetDateTime fecha
) {

    public static ObservacionResponse from(ObservacionPedagogica o) {
        return new ObservacionResponse(
                o.getId(),
                o.getEstudianteId(),
                o.getUnidadId(),
                o.getDocenteId(),
                o.getTexto(),
                o.getFecha()
        );
    }
}