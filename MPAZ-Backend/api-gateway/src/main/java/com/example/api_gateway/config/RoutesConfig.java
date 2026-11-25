package com.example.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
                               @Value("${services.auth}") String auth,
                               @Value("${services.unidad}") String unidad,
                               @Value("${services.asignacion}") String asignacion,
                               @Value("${services.evaluacion}") String evaluacion) {
        return builder.routes()
            .route("auth-service", r -> r.path("/auth/**").uri(auth))
            .route("unidad-service", r -> r.path("/unidades/**").uri(unidad))
            .route("asignacion-service", r -> r.path("/asignaciones/**").uri(asignacion))
            .route("evaluacion-service", r -> r
                .path("/desafios/**", "/pruebas/**", "/informes/**", "/rendimiento/**", "/observaciones/**")
                .uri(evaluacion))
            .build();
    }
}
