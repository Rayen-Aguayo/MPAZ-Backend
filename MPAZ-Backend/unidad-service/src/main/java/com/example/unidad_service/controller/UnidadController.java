package com.example.unidad_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.unidad_service.dto.*;
import com.example.unidad_service.service.PreguntaService;
import com.example.unidad_service.service.UnidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * El gateway ya validó el token y agrega X-User-Id / X-User-Role.
 * Escribe solo UTP; leen UTP, DOCENTE y ESTUDIANTE.
 */
@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
public class UnidadController {

    private final UnidadService unidades;
    private final PreguntaService preguntas;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadDto crear(@Valid @RequestBody UnidadRequest r, @RequestHeader("X-User-Id") Long userId) {
        return unidades.crear(r, userId);
    }

    @PutMapping("/{id}")
    public UnidadDto editar(@PathVariable Long id, @Valid @RequestBody UnidadRequest r) {
        return unidades.editar(id, r);
    }

    @GetMapping
    public List<UnidadDto> listar(@RequestParam(required = false) String estado,
                                  @RequestHeader("X-User-Role") String role) {
        return unidades.listar(role, estado);
    }

    @GetMapping("/{id}")
    public UnidadDto obtener(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        return unidades.obtener(id, role);
    }

    @PatchMapping("/{id}/estado")
    public UnidadDto cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest r) {
        return unidades.cambiarEstado(id, r.estado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        unidades.eliminar(id);
    }

    // ---------- Preguntas (desafíos y prueba final) ----------

    @PostMapping("/{id}/preguntas")
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaDto crearPregunta(@PathVariable Long id, @Valid @RequestBody PreguntaRequest r) {
        return preguntas.crear(id, r);
    }

    @GetMapping("/{id}/preguntas")
    public List<PreguntaDto> listarPreguntas(@PathVariable Long id,
                                             @RequestParam(required = false) String tipo,
                                             @RequestHeader("X-User-Role") String role) {
        return preguntas.listar(id, tipo, role);
    }

    @PutMapping("/{id}/preguntas/{preguntaId}")
    public PreguntaDto editarPregunta(@PathVariable Long id, @PathVariable Long preguntaId,
                                      @Valid @RequestBody PreguntaRequest r) {
        return preguntas.editar(id, preguntaId, r);
    }

    @DeleteMapping("/{id}/preguntas/{preguntaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPregunta(@PathVariable Long id, @PathVariable Long preguntaId) {
        preguntas.eliminar(id, preguntaId);
    }
}
