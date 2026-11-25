--liquibase formatted sql

--changeset mpaz:1
CREATE TABLE unidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asignatura VARCHAR(60) NOT NULL,
    titulo VARCHAR(60) NOT NULL,
    codigo_oa VARCHAR(20) NOT NULL,
    objetivo TEXT NOT NULL,
    explicacion TEXT NOT NULL,
    ejemplo TEXT NOT NULL,
    instrucciones TEXT NULL,
    imagen_uri LONGTEXT NULL,
    estado VARCHAR(20) NOT NULL,
    creador_id BIGINT NOT NULL,
    creada_en BIGINT NOT NULL
);

--changeset mpaz:2
CREATE TABLE preguntas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidad_id BIGINT NOT NULL,
    texto VARCHAR(300) NOT NULL,
    alternativa_a VARCHAR(100) NOT NULL,
    alternativa_b VARCHAR(100) NOT NULL,
    alternativa_c VARCHAR(100) NOT NULL,
    alternativa_d VARCHAR(100) NOT NULL,
    correcta INT NOT NULL,
    es_prueba_final BOOLEAN NOT NULL,
    orden INT NOT NULL,
    CONSTRAINT fk_pregunta_unidad FOREIGN KEY (unidad_id) REFERENCES unidades(id) ON DELETE CASCADE
);
