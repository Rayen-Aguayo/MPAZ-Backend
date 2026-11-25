Asignación Service – Microservicio de Asignaciones (MPAZ PRO)

1. Descripción

El microservicio asignacion-service permite al docente asignar una unidad autorizada a un curso, definiendo su disponibilidad, los intentos máximos de la prueba y si los estudiantes pueden ver sus respuestas.

Reglas:

Solo se pueden asignar unidades AUTORIZADAS.
Una unidad se asigna una sola vez por curso.
La fecha de fin no puede ser anterior a la de inicio.
Las fechas se guardan en milisegundos (medianoche UTC, como entrega el selector de fecha de la app).
El estudiante ve solo las asignaciones de su curso, que viene en su token.

2. Tecnologías

Java 21
Spring Boot 4.0.10
Spring Web
Spring Data JPA
MySQL
Liquibase
Maven
API REST
JSON

3. URL base

http://localhost:9090/asignaciones

(Se entra siempre por el api-gateway. El servicio corre en el puerto 9092; Base de datos: bd_asignacion)

4. Endpoints

Asignar unidad a un curso (DOCENTE)

POST /asignaciones

```json
{
    "unidadId": 1,
    "curso": "2° Básico A",
    "fechaInicio": 1764201600000,
    "fechaFin": 1766793600000,
    "maxIntentos": 3,
    "notificarCurso": true,
    "permitirVerRespuestas": true
}
```

Respuesta (201):

```json
{
    "id": 1,
    "unidadId": 1,
    "docenteId": 2,
    "curso": "2° Básico A",
    "fechaInicio": 1764201600000,
    "fechaFin": 1766793600000,
    "maxIntentos": 3,
    "notificarCurso": true,
    "permitirVerRespuestas": true,
    "vigente": true,
    "unidad": { "id": 1, "asignatura": "Lenguaje", "titulo": "Los cuentos", "codigoOA": "OA 3", "objetivo": "Leer cuentos breves", "estado": "AUTORIZADA" }
}
```

Mis asignaciones como docente (DOCENTE): GET /asignaciones/docente

Asignaciones de mi curso (ESTUDIANTE): GET /asignaciones/mias

Ver una asignación: GET /asignaciones/{id}

Eliminar asignación (DOCENTE que la creó): DELETE /asignaciones/{id}

5. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar todos los microservicios y el api-gateway.
Tener una unidad AUTORIZADA (ver unidad-service).
Hacer login como DOCENTE y copiar el token.
Enviar POST /asignaciones.
Hacer login como ESTUDIANTE del mismo curso y consultar GET /asignaciones/mias.

6. Integración con otros microservicios

asignacion-service consulta a unidad-service (por /internal/unidades) para validar la unidad. evaluacion-service consulta a asignacion-service (por /internal/asignaciones) para validar fechas, curso e intentos.

Los endpoints públicos se envían con el encabezado:

```http
Authorization: Bearer TU_TOKEN
```
