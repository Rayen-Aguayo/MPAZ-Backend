package com.example.evaluacion_service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.example.evaluacion_service.dto.PreguntaConEstado;
import com.example.evaluacion_service.dto.PreguntaDto;
import com.example.evaluacion_service.dto.UnidadResumen;

@Component
public class UnidadClient {

    private final RestClient http;

    public UnidadClient(@Value("${services.unidad.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public UnidadResumen unidad(Long id) {
        try {
            return http.get().uri("/internal/unidades/{id}", id).retrieve().body(UnidadResumen.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La unidad no existe");
        } catch (RestClientException e) {
            throw caido();
        }
    }

    public PreguntaConEstado pregunta(Long id) {
        try {
            return http.get().uri("/internal/preguntas/{id}", id).retrieve().body(PreguntaConEstado.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pregunta no existe");
        } catch (RestClientException e) {
            throw caido();
        }
    }

    /** tipo = DESAFIO | PRUEBA. Incluye la respuesta correcta (uso interno). */
    public List<PreguntaDto> preguntas(Long unidadId, String tipo) {
        try {
            List<PreguntaDto> lista = http.get()
                .uri("/internal/unidades/{id}/preguntas?tipo={tipo}", unidadId, tipo)
                .retrieve()
                .body(new ParameterizedTypeReference<List<PreguntaDto>>() {});
            return lista == null ? List.of() : lista;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La unidad no existe");
        } catch (RestClientException e) {
            throw caido();
        }
    }

    private ResponseStatusException caido() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "unidad-service no responde");
    }
}
