package com.example.unidad_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unidades")
@Getter
@Setter
@NoArgsConstructor
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asignatura;
    private String titulo;
    /** Ej.: "OA 3" */
    @Column(name = "codigo_oa")
    private String codigoOA;
    @Column(columnDefinition = "TEXT")
    private String objetivo;
    @Column(columnDefinition = "TEXT")
    private String explicacion;
    @Column(columnDefinition = "TEXT")
    private String ejemplo;
    /** Texto que la app lee en voz alta (TextToSpeech) */
    @Column(columnDefinition = "TEXT")
    private String instrucciones;
    /** Imagen de apoyo (URL o base64) */
    @Column(columnDefinition = "LONGTEXT")
    private String imagenUri;

    @Enumerated(EnumType.STRING)
    private EstadoUnidad estado = EstadoUnidad.BORRADOR;

    private Long creadorId;
    private Long creadaEn;
}