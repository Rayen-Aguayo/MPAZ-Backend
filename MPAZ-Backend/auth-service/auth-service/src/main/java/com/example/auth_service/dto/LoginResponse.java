package com.example.auth_service.dto;

public record LoginResponse(String token, Long id, String nombre, String username, String role, String curso) {
}
