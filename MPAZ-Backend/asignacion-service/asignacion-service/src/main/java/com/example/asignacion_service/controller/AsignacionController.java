package com.example.asignacion_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.asignacion_service.dto.AsignacionDto;
import com.example.asignacion_service.dto.AsignacionRequest;
import com.example.asignacion_service.service.AsignacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService service;

    /** Docente: asigna una unidad autorizada a un curso. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AsignacionDto crear(@Valid @RequestBody AsignacionRequest r,
                               @RequestHeader("X-User-Id") Long docenteId) {
        return service.crear(r, docenteId);
    }

    /** Docente: sus asignaciones. */
    @GetMapping("/docente")
    public List<AsignacionDto> delDocente(@RequestHeader("X-User-Id") Long docenteId) {
        return service.delDocente(docenteId);
    }

    /** Estudiante: asignaciones de su curso (el curso viene del token). */
    @GetMapping("/mias")
    public List<AsignacionDto> mias(@RequestHeader("X-User-Curso") String curso) {
        return service.delCurso(curso);
    }

    @GetMapping("/{id}")
    public AsignacionDto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @RequestHeader("X-User-Id") Long docenteId) {
        service.eliminar(id, docenteId);
    }
}
