package com.example.evaluacion_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResolverRequest(@NotNull @Min(0) @Max(3) Integer elegida) {
}
