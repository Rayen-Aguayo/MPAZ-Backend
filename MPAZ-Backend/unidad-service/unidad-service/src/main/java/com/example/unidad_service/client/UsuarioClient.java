package com.example.unidad_service.client;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Cliente HTTP hacia usuario-service para validar perfiles UTP. */
@Component
public class UsuarioClient {

    private static final Logger log = LoggerFactory.getLogger(UsuarioClient.class);

    private final RestClient client;
    private final boolean validarRemoto;

    public UsuarioClient(
            RestClient.Builder builder,
            @Value("${clients.usuario-service.url:http://localhost:8081}") String url,
            @Value("${clients.usuario-service.enabled:false}") boolean validarRemoto) {
        this.client = builder.baseUrl(url).build();
        this.validarRemoto = validarRemoto;
    }

    public Optional<UsuarioRemoto> obtenerUsuario(Long id) {
        if (!validarRemoto) {
            return Optional.empty();
        }
        try {
            UsuarioRemoto usuario =
                    client.get()
                            .uri("/api/usuarios/{id}", id)
                            .retrieve()
                            .body(UsuarioRemoto.class);
            return Optional.ofNullable(usuario);
        } catch (RestClientException ex) {
            log.warn("No se pudo consultar usuario-service para id={}: {}", id, ex.getMessage());
            return Optional.empty();
        }
    }

    public boolean esUsuarioUtpValido(Long usuarioId) {
        if (!validarRemoto) {
            return usuarioId != null && usuarioId > 0;
        }
        return obtenerUsuario(usuarioId)
                .map(u -> "UTP".equalsIgnoreCase(u.rol()))
                .orElse(false);
    }

    public record UsuarioRemoto(Long id, String nombre, String username, String rol) {}
}
