package com.example.evaluacion_service.DTO;

import java.math.BigDecimal;

public record RendimientoResponse(
        Long evaluacionId,
        Long estudianteId,
        Long cantidadIntentos,
        BigDecimal mejorNota,
        BigDecimal promedio
) {}