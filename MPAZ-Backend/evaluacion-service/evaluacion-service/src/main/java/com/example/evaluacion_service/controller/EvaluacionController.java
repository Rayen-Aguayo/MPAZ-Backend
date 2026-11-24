package com.example.evaluacion_service.controller;

import com.example.evaluacion_service.DTO.*;
import com.example.evaluacion_service.service.EvaluacionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    private final EvaluacionService service;

    public EvaluacionController(EvaluacionService s) {
        service = s;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluacionResponse crear(@Valid @RequestBody CrearEvaluacionRequest r) {
        return service.crear(r);
    }

    @GetMapping
    public List<EvaluacionResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EvaluacionResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping("/{id}/intentos")
    @ResponseStatus(HttpStatus.CREATED)
    public IntentoResponse iniciar(@PathVariable Long id, @RequestParam Long estudianteId) {
        return service.iniciar(id, estudianteId);
    }

    @PostMapping("/intentos/{intentoId}/finalizar")
    public IntentoResponse finalizar(@PathVariable Long intentoId, @Valid @RequestBody FinalizarIntentoRequest r) {
        return service.finalizar(intentoId, r);
    }

    @GetMapping("/{id}/intentos")
    public List<IntentoResponse> intentos(@PathVariable Long id, @RequestParam Long estudianteId) {
        return service.intentosDeAlumno(id, estudianteId);
    }

    @GetMapping("/intentos/{intentoId}/respuestas")
    public List<RespuestaDetalleResponse> respuestas(@PathVariable Long intentoId) {
        return service.respuestas(intentoId);
    }
}