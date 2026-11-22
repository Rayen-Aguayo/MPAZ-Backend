package com.example.api_gateway.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                // Nunca se expone lo interno
                .pathMatchers("/internal/**").denyAll()

                // AUTH
                .pathMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()
                .pathMatchers("/auth/estudiantes/**").hasAnyRole("DOCENTE", "UTP")

                // UNIDADES: leen todos los roles, escribe solo UTP
                .pathMatchers(HttpMethod.GET, "/unidades/**").authenticated()
                .pathMatchers("/unidades/**").hasRole("UTP")

                // ASIGNACIONES
                .pathMatchers(HttpMethod.GET, "/asignaciones/mias").hasRole("ESTUDIANTE")
                .pathMatchers(HttpMethod.GET, "/asignaciones/docente").hasRole("DOCENTE")
                .pathMatchers(HttpMethod.GET, "/asignaciones/*").authenticated()
                .pathMatchers("/asignaciones/**").hasRole("DOCENTE")

                // EVALUACION
                .pathMatchers("/desafios/**").hasRole("ESTUDIANTE")
                .pathMatchers("/pruebas/**").hasRole("ESTUDIANTE")
                .pathMatchers("/informes/**", "/rendimiento/**").hasAnyRole("DOCENTE", "UTP")
                .pathMatchers(HttpMethod.GET, "/observaciones/**").authenticated()
                .pathMatchers("/observaciones/**").hasRole("DOCENTE")

                .anyExchange().authenticated())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(rolConverter())))
            .build();
    }

    /** Convierte el claim "role" del token en la autoridad ROLE_xxx. */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> rolConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> autoridades = new ArrayList<>();
            String role = jwt.getClaimAsString("role");
            if (role != null) {
                autoridades.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return autoridades;
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}
