package com.example.unidad_service.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadoRequest(@NotBlank String estado) {
}
