package com.example.unidad_service.DTO;

import com.example.unidad_service.model.EstadoUnidad;
import jakarta.validation.constraints.NotNull;

/** DTO para solicitar transición de estado de una unidad didáctica. */
public record CambioEstadoRequestDTO(
        @NotNull(message = "El nuevo estado es obligatorio") EstadoUnidad estado) {}
