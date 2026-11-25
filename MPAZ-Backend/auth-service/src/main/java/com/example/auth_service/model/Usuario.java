package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    /** UTP, DOCENTE o ESTUDIANTE */
    @Column(nullable = false)
    private String role;

    /** Solo estudiantes, ej.: "2° Básico A" */
    private String curso;
}
