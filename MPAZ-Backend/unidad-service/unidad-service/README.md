# unidad-service

Microservicio REST desarrollado con **Spring Boot 3**, **Java 17**, **Maven**, **Liquibase** y **Docker** enfocado exclusivamente en la gestión de unidades didácticas por Objetivo de Aprendizaje (ecosistema **MPAZ PRO 2.º**).

## Estructura de directorios

```text
unidad-service/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
└── src/main/
    ├── java/com/example/unidad_service/
    │   ├── UnidadServiceApplication.java
    │   ├── model/          # Entidad JPA Unidad y enum EstadoUnidad
    │   ├── repository/     # Repositorio Spring Data JPA (UnidadRepository)
    │   ├── DTO/            # Objetos de transferencia de datos (Request / Response / Error)
    │   ├── client/         # Clientes HTTP RestClient (UsuarioClient, AsignacionClient)
    │   ├── service/        # Lógica de negocio y máquina de estados (UnidadService, UnidadServiceImpl)
    │   └── controller/     # Controlador REST (UnidadController) y manejo global de excepciones
    └── resources/
        ├── application.yml # Configuración YAML (perfil local H2 + perfil docker PostgreSQL)
        └── db/changelog/   # Migraciones Liquibase
```

## Ejecución local (Maven + H2)

```bash
mvn spring-boot:run
```

Levanta en `http://localhost:8082` con base de datos H2 en memoria y migraciones automáticas de Liquibase.

## Ejecución con Docker

### Con Docker Compose (incluye PostgreSQL 16)

```bash
docker compose up --build
```

### Solo contenedor (con H2 en memoria por defecto)

```bash
docker build -t unidad-service:latest .
docker run -p 8082:8082 unidad-service:latest
```
