--liquibase formatted sql

--changeset mpaz:1
CREATE TABLE avance_desafios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    pregunta_id BIGINT NOT NULL,
    unidad_id BIGINT NOT NULL,
    fecha BIGINT NOT NULL,
    CONSTRAINT uq_avance UNIQUE (estudiante_id, pregunta_id)
);

--changeset mpaz:2
CREATE TABLE intentos_prueba (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    unidad_id BIGINT NOT NULL,
    curso VARCHAR(30) NOT NULL,
    codigo_oa VARCHAR(20) NOT NULL,
    objetivo TEXT NOT NULL,
    asignatura VARCHAR(60) NOT NULL,
    aciertos INT NOT NULL,
    total INT NOT NULL,
    nota DOUBLE NOT NULL,
    fecha BIGINT NOT NULL
);
CREATE INDEX idx_intentos_asignacion ON intentos_prueba (asignacion_id, estudiante_id);

--changeset mpaz:3
CREATE TABLE respuestas_prueba (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intento_id BIGINT NOT NULL,
    pregunta_id BIGINT NOT NULL,
    elegida INT NULL,
    correcta BOOLEAN NOT NULL,
    CONSTRAINT fk_respuesta_intento FOREIGN KEY (intento_id) REFERENCES intentos_prueba(id) ON DELETE CASCADE
);

--changeset mpaz:4
CREATE TABLE observaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    unidad_id BIGINT NOT NULL,
    docente_id BIGINT NOT NULL,
    texto VARCHAR(500) NOT NULL,
    fecha BIGINT NOT NULL
);
