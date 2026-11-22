package com.example.auth_service.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.LoginResponse;
import com.example.auth_service.dto.RegistroRequest;
import com.example.auth_service.dto.UsuarioDto;
import com.example.auth_service.model.Usuario;
import com.example.auth_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Set<String> ROLES = Set.of("UTP", "DOCENTE", "ESTUDIANTE");

    private final UsuarioRepository repo;
    private final HashService hashService;
    private final JwtService jwtService;

    public UsuarioDto registrar(RegistroRequest r) {
        String role = r.role().trim().toUpperCase();
        if (!ROLES.contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido: use UTP, DOCENTE o ESTUDIANTE");
        }
        String curso = null;
        if (role.equals("ESTUDIANTE")) {
            if (r.curso() == null || r.curso().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estudiante debe indicar su curso");
            }
            curso = r.curso().trim();
        }
        String username = r.username().trim();
        if (repo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }
        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setUsername(username);
        u.setPasswordHash(hashService.hash(r.password()));
        u.setRole(role);
        u.setCurso(curso);
        return aDto(repo.save(u));
    }

    public LoginResponse login(LoginRequest r) {
        Usuario u = repo.findByUsername(r.username().trim())
            .filter(x -> hashService.verifica(r.password(), x.getPasswordHash()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o clave incorrectos"));
        return new LoginResponse(jwtService.generateToken(u), u.getId(), u.getNombre(),
                u.getUsername(), u.getRole(), u.getCurso());
    }

    public List<UsuarioDto> estudiantes(String curso) {
        List<Usuario> lista = (curso == null || curso.isBlank())
            ? repo.findByRole("ESTUDIANTE")
            : repo.findByRoleAndCurso("ESTUDIANTE", curso);
        return lista.stream().map(this::aDto).toList();
    }

    private UsuarioDto aDto(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getUsername(), u.getRole(), u.getCurso());
    }
}
