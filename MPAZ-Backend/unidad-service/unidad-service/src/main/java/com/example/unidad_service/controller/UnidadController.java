package com.example.unidad_service.controller;

import com.example.unidad_service.DTO.CambioEstadoRequestDTO;
import com.example.unidad_service.DTO.UnidadRequestDTO;
import com.example.unidad_service.DTO.UnidadResponseDTO;
import com.example.unidad_service.model.EstadoUnidad;
import com.example.unidad_service.service.UnidadService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/unidades")
@CrossOrigin(origins = "*")
public class UnidadController {

    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    @GetMapping
    public ResponseEntity<List<UnidadResponseDTO>> listarUnidades(
            @RequestParam(required = false) EstadoUnidad estado,
            @RequestParam(required = false) String asignatura) {
        return ResponseEntity.ok(unidadService.listarUnidades(estado, asignatura));
    }

    @GetMapping("/autorizadas")
    public ResponseEntity<List<UnidadResponseDTO>> listarAutorizadas() {
        return ResponseEntity.ok(unidadService.listarAutorizadas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UnidadResponseDTO> crearUnidad(
            @Valid @RequestBody UnidadRequestDTO request) {
        UnidadResponseDTO creada = unidadService.crearUnidad(request);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(creada.id())
                        .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadResponseDTO> actualizarUnidad(
            @PathVariable Long id, @Valid @RequestBody UnidadRequestDTO request) {
        return ResponseEntity.ok(unidadService.actualizarUnidad(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UnidadResponseDTO> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody CambioEstadoRequestDTO request) {
        return ResponseEntity.ok(unidadService.cambiarEstado(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUnidad(@PathVariable Long id) {
        unidadService.eliminarUnidad(id);
        return ResponseEntity.noContent().build();
    }
}
