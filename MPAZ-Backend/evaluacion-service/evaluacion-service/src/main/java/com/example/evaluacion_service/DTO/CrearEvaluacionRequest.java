package com.example.evaluacion_service.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CrearEvaluacionRequest(
        @NotNull @Positive Long unidadId,
        @NotNull @Positive Long asignacionId,
        @NotBlank @Size(max = 160) String titulo,
        @NotNull @DecimalMin("1.00") @DecimalMax("100.00") BigDecimal porcentajeExigencia,
        @NotNull @Min(1) @Max(500) Integer puntajeMaximo
) {}
