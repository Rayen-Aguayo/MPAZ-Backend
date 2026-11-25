package com.example.evaluacion_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.evaluacion_service.dto.IntentoDto;
import com.example.evaluacion_service.dto.IntentoRequest;
import com.example.evaluacion_service.dto.MisIntentos;
import com.example.evaluacion_service.service.PruebaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pruebas")
@RequiredArgsConstructor
public class PruebaController {

    private final PruebaService service;

    /** Entrega un intento de la prueba final. */
    @PostMapping("/{asignacionId}/intentos")
    @ResponseStatus(HttpStatus.CREATED)
    public IntentoDto rendir(@PathVariable Long asignacionId,
                             @Valid @RequestBody IntentoRequest r,
                             @RequestHeader("X-User-Id") Long estudianteId,
                             @RequestHeader("X-User-Curso") String curso) {
        return service.rendir(estudianteId, curso, asignacionId, r);
    }

    @GetMapping("/{asignacionId}/mis-intentos")
    public MisIntentos misIntentos(@PathVariable Long asignacionId,
                                   @RequestHeader("X-User-Id") Long estudianteId,
                                   @RequestHeader("X-User-Curso") String curso) {
        return service.misIntentos(estudianteId, curso, asignacionId);
    }
}
