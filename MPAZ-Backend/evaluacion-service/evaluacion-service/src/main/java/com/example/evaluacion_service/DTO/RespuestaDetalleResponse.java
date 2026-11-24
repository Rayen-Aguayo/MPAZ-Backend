package com.example.evaluacion_service.DTO;

public record RespuestaDetalleResponse(
        Long preguntaId,
        Integer alternativaSeleccionada,
        Boolean correcta
) {}