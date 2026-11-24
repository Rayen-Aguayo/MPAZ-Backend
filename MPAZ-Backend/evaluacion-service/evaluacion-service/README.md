# evaluacion-service — MPAZ PRO

Microservicio Java 17 / Spring Boot / Maven / PostgreSQL / Liquibase para gestionar pruebas finales, intentos, respuestas, notas y observaciones pedagógicas.

## Estructura
- `model`: entidades JPA y enumeradores.
- `repository`: persistencia Spring Data JPA.
- `controller`: API REST y manejo de errores.
- `service`: reglas de negocio y cálculo de calificaciones.
- `DTO`: contratos de entrada/salida.
- `client`: integración HTTP con `unidad-service` y `asignacion-service`.
- `src/main/resources/db/changelog`: migraciones Liquibase.

## Reglas heredadas de MPAZ PRO
- Las preguntas tienen cuatro alternativas: A=0, B=1, C=2, D=3.
- Solo se consideran preguntas marcadas como prueba final.
- Cada asignación determina el máximo de intentos.
- La calificación se expresa de 1,0 a 7,0; por defecto, 60% correcto equivale a 4,0 y 100% a 7,0.
- Se persiste el intento junto con sus respuestas dentro de una transacción.
- Los IDs de estudiante, unidad, asignación, docente y pregunta son referencias externas, no entidades duplicadas.

## Requisitos
- JDK 17+
- Maven 3.9+
- Docker (opcional, para PostgreSQL)

## Ejecución local
1. Levantar PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
Liquibase crea el esquema automáticamente al iniciar. Hibernate usa `ddl-auto=validate`, por lo que no crea ni modifica tablas.

Variables configurables:
- `DB_URL` (por defecto `jdbc:postgresql://localhost:5432/mpaz_evaluaciones`)
- `DB_USERNAME` (por defecto `mpaz`)
- `DB_PASSWORD` (por defecto `mpaz`)
- `SERVER_PORT` (por defecto `8084`)
- `UNIDAD_SERVICE_URL` (por defecto `http://localhost:8082`)
- `ASIGNACION_SERVICE_URL` (por defecto `http://localhost:8083`)

## Contrato requerido de servicios externos

### unidad-service
`GET /api/unidades/{unidadId}/preguntas?esPruebaFinal=true`

Respuesta esperada:
```json
[
  {"id": 201, "correcta": 2},
  {"id": 202, "correcta": 0}
]
```
`correcta` mantiene la convención 0=A, 1=B, 2=C, 3=D. Este endpoint es interno: no debe exponerse la clave de respuestas al cliente móvil.

### asignacion-service
`GET /api/asignaciones/{asignacionId}`

Respuesta esperada:
```json
{
  "id": 30,
  "unidadId": 12,
  "maxIntentos": 2,
  "estado": "ACTIVA"
}
```
El servicio verifica que la asignación corresponda a la unidad y que el estudiante no exceda los intentos. Los servicios externos deben estar disponibles al iniciar/finalizar un intento.

## Endpoints

| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/evaluaciones` | Crear evaluación |
| GET | `/api/evaluaciones` | Listar |
| GET | `/api/evaluaciones/{id}` | Obtener |
| POST | `/api/evaluaciones/{id}/intentos?estudianteId=10` | Iniciar intento |
| POST | `/api/evaluaciones/intentos/{intentoId}/finalizar` | Enviar respuestas y calificar |
| GET | `/api/evaluaciones/{id}/intentos?estudianteId=10` | Historial de intentos |
| GET | `/api/evaluaciones/intentos/{intentoId}/respuestas` | Revisión posterior |
| POST | `/api/observaciones` | Registrar observación |
| GET | `/api/observaciones/alumno/{estudianteId}?unidadId=12` | Consultar observaciones |

### Ejemplo: crear evaluación
```json
{
  "unidadId": 12,
  "asignacionId": 30,
  "titulo": "Prueba final: sumas y restas",
  "porcentajeExigencia": 60.00,
  "puntajeMaximo": 20
}
```

### Ejemplo: iniciar intento
`POST /api/evaluaciones/1/intentos?estudianteId=10`

### Ejemplo: finalizar
```json
{
  "respuestas": [
    {"preguntaId": 201, "elegida": 2},
    {"preguntaId": 202, "elegida": 0}
  ]
}
```

## Documentación
- Swagger UI: `http://localhost:8084/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8084/v3/api-docs`


## Consideraciones antes de producción
- Incorporar autenticación/autorización (por ejemplo JWT desde auth-service); actualmente los IDs se reciben como parámetros/DTO y no se valida la identidad del solicitante.
- Proteger los endpoints internos que consultan claves de respuestas.
- Definir política de cierre/expiración de intentos y fechas de vigencia.
- Para evitar carreras concurrentes al consumir el último intento, se recomienda añadir bloqueo optimista/pesimista o una restricción transaccional coordinada en asignacion-service.
- El endpoint de unidad-service debe devolver una instantánea estable de las preguntas durante una prueba; para máxima trazabilidad, conviene persistir la clave/versión de cada pregunta al iniciar el intento.
- El resumen de rendimiento por OA y curso requiere datos de asignación, curso y objetivos; puede implementarse mediante consultas agregadas o un endpoint de reporting cuando esos contratos estén disponibles.
