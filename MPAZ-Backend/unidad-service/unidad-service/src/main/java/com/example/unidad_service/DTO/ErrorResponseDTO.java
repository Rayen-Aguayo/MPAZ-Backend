package com.example.unidad_service.DTO;

import java.time.LocalDateTime;
import java.util.Map;

/** DTO estándar para respuestas de error HTTP. */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {}
