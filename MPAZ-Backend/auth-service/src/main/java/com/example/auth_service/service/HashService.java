package com.example.auth_service.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;

/** PBKDF2 con sal aleatoria (formato guardado: iteraciones:sal:hash). */
@Service
public class HashService {

    private static final int ITERACIONES = 120_000;
    private static final int BITS = 256;

    public String hash(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        byte[] h = pbkdf2(clave, sal, ITERACIONES);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(sal) + ":" + Base64.getEncoder().encodeToString(h);
    }

    public boolean verifica(String clave, String guardado) {
        try {
            String[] p = guardado.split(":");
            int it = Integer.parseInt(p[0]);
            byte[] sal = Base64.getDecoder().decode(p[1]);
            byte[] esperado = Base64.getDecoder().decode(p[2]);
            byte[] actual = pbkdf2(clave, sal, it);
            return MessageDigest.isEqual(esperado, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] pbkdf2(String clave, byte[] sal, int iteraciones) {
        try {
            KeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, iteraciones, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }
}
