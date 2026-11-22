package com.example.asignacion_service.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.example.asignacion_service.dto.UnidadResumen;

@Component
public class UnidadClient {

    private final RestClient http;

    public UnidadClient(@Value("${services.unidad.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public UnidadResumen obtener(Long id) {
        try {
            return http.get().uri("/internal/unidades/{id}", id).retrieve().body(UnidadResumen.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La unidad no existe");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "unidad-service no responde");
        }
    }

    public Map<Long, UnidadResumen> porIds(Collection<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        try {
            String lista = ids.stream().distinct().map(String::valueOf).collect(Collectors.joining(","));
            List<UnidadResumen> unidades = http.get()
                .uri("/internal/unidades?ids={ids}", lista)
                .retrieve()
                .body(new ParameterizedTypeReference<List<UnidadResumen>>() {});
            return unidades == null ? Map.of()
                : unidades.stream().collect(Collectors.toMap(UnidadResumen::id, Function.identity()));
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "unidad-service no responde");
        }
    }
}
