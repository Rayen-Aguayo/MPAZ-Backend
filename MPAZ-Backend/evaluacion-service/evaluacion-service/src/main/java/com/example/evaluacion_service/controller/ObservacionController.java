package com.example.evaluacion_service.controller;

import com.example.evaluacion_service.DTO.*;
import com.example.evaluacion_service.service.EvaluacionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observaciones")
public class ObservacionController {

    private final EvaluacionService service;

    public ObservacionController(EvaluacionService s) {
        service = s;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObservacionResponse crear(@Valid @RequestBody ObservacionRequest r) {
        return service.crearObservacion(r);
    }

    @GetMapping("/alumno/{estudianteId}")
    public List<ObservacionResponse> listar(@PathVariable Long estudianteId, @RequestParam(required = false) Long unidadId) {
        return service.observaciones(estudianteId, unidadId);
    }
}