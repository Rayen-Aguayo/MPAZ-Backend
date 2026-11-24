package com.example.unidad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidad que representa una unidad didáctica asociada a un Objetivo de Aprendizaje (OA).
 */
@Entity
@Table(name = "unidades")
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String asignatura;

    @Column(nullable = false, length = 60)
    private String titulo;

    @Column(name = "codigo_oa", nullable = false, length = 10)
    private String codigoOA;

    @Column(nullable = false, length = 150)
    private String objetivo;

    @Column(nullable = false, length = 300)
    private String explicacion;

    @Column(nullable = false, length = 200)
    private String ejemplo;

    @Column(nullable = false, length = 200)
    private String instrucciones;

    @Column(name = "imagen_uri", length = 500)
    private String imagenUri;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUnidad estado = EstadoUnidad.BORRADOR;

    @Column(name = "creador_id", nullable = false)
    private Long creadorId;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    public Unidad() {}

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoUnidad.BORRADOR;
        }
        if (this.creadaEn == null) {
            this.creadaEn = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCodigoOA() {
        return codigoOA;
    }

    public void setCodigoOA(String codigoOA) {
        this.codigoOA = codigoOA;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public String getEjemplo() {
        return ejemplo;
    }

    public void setEjemplo(String ejemplo) {
        this.ejemplo = ejemplo;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public EstadoUnidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoUnidad estado) {
        this.estado = estado;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }
}
