package com.example.unidad_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP hacia asignacion-service para notificar cambios de disponibilidad
 * cuando una unidad es deshabilitada por UTP.
 */
@Component
public class AsignacionClient {

    private static final Logger log = LoggerFactory.getLogger(AsignacionClient.class);

    private final RestClient client;
    private final boolean enabled;

    public AsignacionClient(
            RestClient.Builder builder,
            @Value("${clients.asignacion-service.url:http://localhost:8083}") String url,
            @Value("${clients.asignacion-service.enabled:false}") boolean enabled) {
        this.client = builder.baseUrl(url).build();
        this.enabled = enabled;
    }

    public void notificarUnidadDeshabilitada(Long unidadId) {
        if (!enabled) {
            return;
        }
        try {
            client.patch()
                    .uri("/api/asignaciones/unidad/{unidadId}/deshabilitar", unidadId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn(
                    "No se pudo notificar deshabilitación de unidadId={} a asignacion-service: {}",
                    unidadId,
                    ex.getMessage());
        }
    }

    public record AsignacionResumenRemota(Long id, Long unidadId, String curso, String estado) {}
}
