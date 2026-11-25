package com.example.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.auth_service.model.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-minutes:240}") long minutos) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = minutos * 60_000L;
    }

    /** Token con sub (username), uid, role y curso (solo estudiantes). */
    public String generateToken(Usuario u) {
        Date ahora = new Date();
        var builder = Jwts.builder()
            .subject(u.getUsername())
            .claim("uid", u.getId())
            .claim("role", u.getRole())
            .issuedAt(ahora)
            .expiration(new Date(ahora.getTime() + expiracionMs));
        if (u.getCurso() != null) {
            builder.claim("curso", u.getCurso());
        }
        return builder.signWith(key, Jwts.SIG.HS256).compact();
    }
}
