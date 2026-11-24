package com.example.evaluacion_service.controller;

import com.example.evaluacion_service.DTO.ApiError;
import com.example.evaluacion_service.service.ReglaNegocioException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    ResponseEntity<ApiError> regla(ReglaNegocioException ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getStatus()).body(
                new ApiError(
                        OffsetDateTime.now(),
                        ex.getStatus().value(),
                        ex.getStatus().getReasonPhrase(),
                        ex.getMessage(),
                        req.getRequestURI(),
                        Map.of()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                new ApiError(
                        OffsetDateTime.now(),
                        400,
                        "Bad Request",
                        "Datos de entrada inválidos.",
                        req.getRequestURI(),
                        errors
                )
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> general(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500).body(
                new ApiError(
                        OffsetDateTime.now(),
                        500,
                        "Internal Server Error",
                        "Ocurrió un error interno.",
                        req.getRequestURI(),
                        Map.of()
                )
        );
    }
}