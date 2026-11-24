package com.example.evaluacion_service.DTO;

import jakarta.validation.constraints.*;

public record ObservacionRequest(
        @NotNull @Positive Long estudianteId,
        @NotNull @Positive Long unidadId,
        @NotNull @Positive Long docenteId,
        @NotBlank @Size(max = 2000) String texto
) {}