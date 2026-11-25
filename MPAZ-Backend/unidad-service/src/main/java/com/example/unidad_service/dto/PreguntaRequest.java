package com.example.unidad_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PreguntaRequest(
        @NotBlank @Size(max = 300) String texto,
        @NotBlank @Size(max = 100) String alternativaA,
        @NotBlank @Size(max = 100) String alternativaB,
        @NotBlank @Size(max = 100) String alternativaC,
        @NotBlank @Size(max = 100) String alternativaD,
        @NotNull @Min(0) @Max(3) Integer correcta,
        @NotNull Boolean esPruebaFinal,
        Integer orden) {
}
