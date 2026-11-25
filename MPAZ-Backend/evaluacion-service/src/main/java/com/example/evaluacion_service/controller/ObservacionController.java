package com.example.evaluacion_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.evaluacion_service.dto.ObservacionRequest;
import com.example.evaluacion_service.model.Observacion;
import com.example.evaluacion_service.service.ObservacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/observaciones")
@RequiredArgsConstructor
public class ObservacionController {

    private final ObservacionService service;

    /** Docente escribe una observación pedagógica. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Observacion crear(@Valid @RequestBody ObservacionRequest r,
                             @RequestHeader("X-User-Id") Long docenteId) {
        return service.crear(r, docenteId);
    }

    /** Docente/UTP consultan por estudiante; el estudiante solo ve las suyas. */
    @GetMapping
    public List<Observacion> listar(@RequestParam(required = false) Long estudianteId,
                                    @RequestParam(required = false) Long unidadId,
                                    @RequestHeader("X-User-Id") Long userId,
                                    @RequestHeader("X-User-Role") String role) {
        Long objetivo = "ESTUDIANTE".equals(role) ? userId : estudianteId;
        if (objetivo == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Falta estudianteId");
        }
        return service.listar(objetivo, unidadId);
    }
}
