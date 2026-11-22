package com.example.asignacion_service.controller;

import org.springframework.web.bind.annotation.*;

import com.example.asignacion_service.dto.AsignacionDto;
import com.example.asignacion_service.service.AsignacionService;

import lombok.RequiredArgsConstructor;

/** Solo para evaluacion-service. El gateway no publica /internal. */
@RestController
@RequestMapping("/internal/asignaciones")
@RequiredArgsConstructor
public class InternalController {

    private final AsignacionService service;

    @GetMapping("/buscar")
    public AsignacionDto buscar(@RequestParam Long unidadId, @RequestParam String curso) {
        return service.buscarPorUnidadYCurso(unidadId, curso);
    }

    @GetMapping("/{id}")
    public AsignacionDto porId(@PathVariable Long id) {
        return service.interna(id);
    }
}
