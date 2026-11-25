package com.example.evaluacion_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.example.evaluacion_service.dto.AsignacionDto;

@Component
public class AsignacionClient {

    private final RestClient http;

    public AsignacionClient(@Value("${services.asignacion.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    /** Devuelve null si no existe. */
    public AsignacionDto porId(Long id) {
        try {
            return http.get().uri("/internal/asignaciones/{id}", id).retrieve().body(AsignacionDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (RestClientException e) {
            throw caido();
        }
    }

    /** Devuelve null si el curso no tiene esa unidad asignada. */
    public AsignacionDto buscar(Long unidadId, String curso) {
        try {
            return http.get()
                .uri("/internal/asignaciones/buscar?unidadId={u}&curso={c}", unidadId, curso)
                .retrieve()
                .body(AsignacionDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (RestClientException e) {
            throw caido();
        }
    }

    private ResponseStatusException caido() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "asignacion-service no responde");
    }
}
