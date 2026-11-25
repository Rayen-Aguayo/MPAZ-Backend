package com.example.evaluacion_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.evaluacion_service.dto.ResolverRequest;
import com.example.evaluacion_service.dto.ResultadoDesafio;
import com.example.evaluacion_service.service.DesafioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/desafios")
@RequiredArgsConstructor
public class DesafioController {

    private final DesafioService service;

    @PostMapping("/{preguntaId}/resolver")
    public ResultadoDesafio resolver(@PathVariable Long preguntaId,
                                     @Valid @RequestBody ResolverRequest r,
                                     @RequestHeader("X-User-Id") Long estudianteId,
                                     @RequestHeader("X-User-Curso") String curso) {
        return service.resolver(estudianteId, curso, preguntaId, r.elegida());
    }

    /** Ids de los desafíos ya completados de una unidad. */
    @GetMapping("/unidad/{unidadId}/avance")
    public List<Long> avance(@PathVariable Long unidadId, @RequestHeader("X-User-Id") Long estudianteId) {
        return service.completados(estudianteId, unidadId);
    }
}
