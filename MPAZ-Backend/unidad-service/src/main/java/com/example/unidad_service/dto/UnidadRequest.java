package com.example.unidad_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadRequest(
        @NotBlank @Size(max = 60) String asignatura,
        @NotBlank @Size(min = 5, max = 60) String titulo,
        @NotBlank @Size(max = 20) String codigoOA,
        @NotBlank String objetivo,
        @NotBlank String explicacion,
        @NotBlank String ejemplo,
        String instrucciones,
        String imagenUri) {
}
