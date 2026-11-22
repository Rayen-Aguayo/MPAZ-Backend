package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Borra cualquier X-User-* que venga del cliente y lo reemplaza por los datos
 * del token ya validado. Los servicios internos confían en estos encabezados.
 */
@Component
public class IdentidadFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .filter(a -> a != null && a.getPrincipal() instanceof Jwt)
            .map(a -> (Jwt) a.getPrincipal())
            .map(jwt -> conIdentidad(exchange, jwt))
            .switchIfEmpty(Mono.fromSupplier(() -> sinIdentidad(exchange)))
            .flatMap(chain::filter);
    }

    private ServerWebExchange conIdentidad(ServerWebExchange exchange, Jwt jwt) {
        Object uid = jwt.getClaims().get("uid");
        String role = jwt.getClaimAsString("role");
        String curso = jwt.getClaimAsString("curso");
        return exchange.mutate().request(r -> r.headers(h -> {
            h.remove("X-User-Id");
            h.remove("X-User-Role");
            h.remove("X-User-Curso");
            if (uid != null) h.set("X-User-Id", String.valueOf(uid));
            if (role != null) h.set("X-User-Role", role);
            if (curso != null) h.set("X-User-Curso", curso);
        })).build();
    }

    private ServerWebExchange sinIdentidad(ServerWebExchange exchange) {
        return exchange.mutate().request(r -> r.headers(h -> {
            h.remove("X-User-Id");
            h.remove("X-User-Role");
            h.remove("X-User-Curso");
        })).build();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

