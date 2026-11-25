# MPAZ-Backend

Backend de **MPAZ PRO 2º** con arquitectura de microservicios. Se entra siempre por el `api-gateway`, que valida el token JWT y autoriza por rol (**UTP**, **DOCENTE** y **ESTUDIANTE**).

## Contenido

1. [Microservicios](#1-microservicios)
2. [Tecnologías](#2-tecnologías)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Requisitos](#4-requisitos)
5. [Preparar MySQL con XAMPP](#5-preparar-mysql-con-xampp)
6. [Levantar el backend con Docker](#6-levantar-el-backend-con-docker)
7. [Pruebas con Postman](#7-pruebas-con-postman)
8. [Autenticación y roles](#8-autenticación-y-roles)
9. [Solución de problemas](#9-solución-de-problemas)
10. [Ejecución sin Docker](#10-ejecución-sin-docker)

---

## 1. Microservicios

| Servicio | Puerto | Base de datos | Función |
|---|---|---|---|
| `api-gateway` | 9090 | — | Punto de entrada, validación del token y permisos por rol |
| `auth-service` | 9094 | `bd_auth` | Registro, login y emisión del JWT |
| `unidad-service` | 9091 | `bd_unidad` | Unidades, desafíos, preguntas de la prueba y estados de autorización |
| `asignacion-service` | 9092 | `bd_asignacion` | Asignación de unidades a cursos por el docente |
| `evaluacion-service` | 9093 | `bd_evaluacion` | Avance, intentos, notas, observaciones e informes |

Solo el `api-gateway` (9090) queda expuesto. Los demás servicios se comunican entre sí dentro de la red de Docker. Cada servicio tiene su propio README con el detalle de sus endpoints.

## 2. Tecnologías

- Java 21
- Spring Boot 4.0.10
- Spring Web / Spring Cloud Gateway
- Spring Data JPA
- MySQL (MariaDB en XAMPP)
- Liquibase
- Maven
- Docker y Docker Compose
- JWT (jjwt 0.12.5)

## 3. Estructura del proyecto

```
MPAZ-Backend/
├── api-gateway/
├── auth-service/
├── unidad-service/
├── asignacion-service/
├── evaluacion-service/
└── docker-compose.yml
```

Cada servicio incluye su `pom.xml`, su `src/` y un `Dockerfile` de **dos etapas**: compila con Maven dentro de Docker y luego ejecuta el `.jar`. Por eso **no hace falta** ejecutar `./mvnw clean package` antes de levantar.

## 4. Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (con el motor encendido: debe decir *Engine running*)
- [XAMPP](https://www.apachefriends.org/) con MySQL en el puerto 3306 (usuario `root`, sin contraseña)
- [Postman](https://www.postman.com/) para probar los endpoints
- Git y VS Code (opcional, para editar el código)

## 5. Preparar MySQL con XAMPP

Los servicios corren dentro de contenedores y se conectan a la MySQL de tu PC mediante `host.docker.internal`. Para que MySQL acepte esas conexiones hay que preparar el usuario `root` una sola vez.

### 5.1 Encender MySQL

1. Abre el **Panel de control de XAMPP**.
2. En la fila de **MySQL**, pulsa **Start** (debe quedar en verde, puerto 3306).
3. Para usar phpMyAdmin, enciende también **Apache** con **Start**.

> XAMPP trae **MariaDB**, no MySQL. Es compatible y funciona igual para este proyecto.

### 5.2 Permitir que Docker se conecte (`root@'%'`)

Ejecuta estas tres líneas en MySQL. Puedes hacerlo de cualquiera de estas formas:

**Opción A: phpMyAdmin**

1. En XAMPP, en la fila de MySQL, pulsa **Admin** (o abre `http://localhost/phpmyadmin`).
2. Haz clic en la pestaña **SQL** (la barra superior, junto a *Bases de datos*).
3. Pega el SQL y pulsa **Continuar**.

**Opción B: consola de XAMPP**

1. En XAMPP pulsa el botón **Shell**.
2. Escribe `mysql -u root`, pega las líneas una a una y termina con `exit`.

**Opción C: PowerShell**

```powershell
& "C:\xampp\mysql\bin\mysql.exe" -u root -e "CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY ''; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'; FLUSH PRIVILEGES;"
```

El SQL es este:

```sql
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
FLUSH PRIVILEGES;
```

Notas:

- Un aviso amarillo `#1046 Base de datos no seleccionada` en phpMyAdmin es normal: no significa que haya fallado.
- Si responde `#1973 ... ya existe`, el usuario ya estaba creado y los permisos igual se aplicaron.
- Para comprobarlo: `SELECT user, host FROM mysql.user;` debe mostrar una fila `root` con host `%`.
- Si tu `root` **tiene contraseña**, ponla en la primera línea (`IDENTIFIED BY 'mi_clave'`) y también en `DB_PASS` del `docker-compose.yml`.
- Si más adelante ves `Access denied for user 'root'`, fija la contraseña vacía con:

  ```sql
  ALTER USER 'root'@'%' IDENTIFIED BY '';
  FLUSH PRIVILEGES;
  ```

### 5.3 Las bases de datos se crean solas

No tienes que crear nada a mano. Cada servicio usa `createDatabaseIfNotExist=true` y Liquibase crea las tablas al arrancar. Cuando todo funcione verás en phpMyAdmin (panel izquierdo) las bases `bd_auth`, `bd_unidad`, `bd_asignacion` y `bd_evaluacion`. Si alguna no aparece, pulsa el icono de recargar del panel; si sigue sin aparecer, ese servicio nunca se conectó a MySQL.

## 6. Levantar el backend con Docker

Todos los comandos se ejecutan en PowerShell desde la carpeta que contiene el `docker-compose.yml` (`MPAZ-Backend\MPAZ-Backend`).

### 6.1 Verificar Docker

Si PowerShell no reconoce el comando `docker`, agrega su ruta a la sesión actual:

```powershell
$env:Path += ";$env:LOCALAPPDATA\Programs\DockerDesktop\resources\bin"
docker version
```

Debe mostrar **Client** y **Server**. Si en *Server* dice `failed to connect`, abre Docker Desktop desde el menú Inicio y espera a que diga *Engine running*.

Para no repetir esa línea, cierra VS Code y todas las ventanas de PowerShell y abre una nueva.

### 6.2 Construir y levantar

```powershell
docker compose up -d --build
```

La primera vez tarda varios minutos: Docker descarga Maven y las dependencias y compila los cinco servicios.

### 6.3 Comprobar el estado

```powershell
docker compose ps -a
```

Los cinco servicios deben aparecer como `running` o `Up`. Si alguno está detenido (`Exited`), mira por qué:

```powershell
docker compose logs unidad-service --tail 60
```

Busca las líneas con `ERROR` y el primer `Caused by:`.

### 6.4 Reconstruir un servicio después de cambiar código

Los contenedores siguen ejecutando el código anterior hasta que se reconstruyen:

```powershell
docker compose up -d --build unidad-service evaluacion-service
```

### 6.5 Otros comandos útiles

```powershell
docker compose down            # apaga y elimina los contenedores
docker compose restart auth-service
docker compose logs -f api-gateway
```

## 7. Pruebas con Postman

Todas las peticiones van al gateway: `http://localhost:9090`.

### 7.1 Orden recomendado

| Paso | Petición | ¿Lleva token? |
|---|---|---|
| 1 | `POST /auth/register` (crear usuario) | No |
| 2 | `POST /auth/login` (aquí se obtiene el token) | No |
| 3 | Todo lo demás | Sí (Bearer) |

**El token lo entrega el login, no el registro.** Primero se crea el usuario y después se hace login.

### 7.2 Registrar los tres usuarios

`POST http://localhost:9090/auth/register`, en **Body → raw → JSON**. Envía uno por uno:

```json
{ "nombre": "Ana UTP", "username": "utp1", "password": "1234", "role": "UTP" }
```

```json
{ "nombre": "Beto Docente", "username": "doc1", "password": "1234", "role": "DOCENTE" }
```

```json
{ "nombre": "Caro Alumna", "username": "est1", "password": "1234", "role": "ESTUDIANTE", "curso": "2° Básico A" }
```

Cada uno responde `201 Created` con su `id`. Si responde `409`, el usuario ya existe.

Ejemplo con curl en PowerShell:

```powershell
curl.exe -X POST http://localhost:9090/auth/register -H "Content-Type: application/json" -d '{\"nombre\":\"Ana UTP\",\"username\":\"utp1\",\"password\":\"1234\",\"role\":\"UTP\"}'
```

### 7.3 Iniciar sesión y obtener el token

`POST http://localhost:9090/auth/login`:

```json
{ "username": "utp1", "password": "1234" }
```

La respuesta trae el campo `token`. Cópialo **sin las comillas**.

### 7.4 Usar el token

En cada petición que no sea login ni registro:

1. Abre la pestaña **Authorization**.
2. En **Auth Type** elige **Bearer Token**.
3. Pega el token en el campo **Token** (sin comillas y sin escribir `Bearer`; Postman lo agrega).

Equivale al encabezado:

```http
Authorization: Bearer TU_TOKEN
```

Cada rol tiene su propio token. Haz login con `utp1`, `doc1` y `est1` y usa el que corresponda a cada paso. El token dura 4 horas.

**Atajo opcional:** en la pestaña **Tests** de la petición de login, pega esto para guardar cada token automáticamente:

```javascript
const data = pm.response.json();
pm.collectionVariables.set("token_" + data.role.toLowerCase(), data.token);
```

Después, en **Authorization** de las demás peticiones, escribe `{{token_utp}}`, `{{token_docente}}` o `{{token_estudiante}}` según el rol.

### 7.5 Flujo completo de prueba

| Paso | Con el token de | Método y URL |
|---|---|---|
| Crear unidad | UTP | `POST /unidades` |
| Crear desafío y pregunta de prueba final | UTP | `POST /unidades/1/preguntas` (dos veces) |
| Enviar a revisión | UTP | `PATCH /unidades/1/estado` |
| Autorizar | UTP | `PATCH /unidades/1/estado` |
| Asignar al curso | DOCENTE | `POST /asignaciones` |
| Ver mis asignaciones | ESTUDIANTE | `GET /asignaciones/mias` |
| Resolver desafío | ESTUDIANTE | `POST /desafios/1/resolver` |
| Entregar prueba | ESTUDIANTE | `POST /pruebas/1/intentos` |
| Ver informes y rendimiento | DOCENTE o UTP | `GET /informes/...`, `GET /rendimiento` |

**Crear unidad** (`POST /unidades`, token UTP):

```json
{
  "asignatura": "Lenguaje",
  "titulo": "Los cuentos",
  "codigoOA": "OA 3",
  "objetivo": "Leer cuentos breves",
  "explicacion": "Un cuento tiene inicio, desarrollo y final.",
  "ejemplo": "Caperucita Roja"
}
```

Responde `201` con la unidad en estado `BORRADOR`.

**Crear desafío** (`POST /unidades/1/preguntas`, token UTP):

```json
{
  "texto": "¿Quién entra al bosque?",
  "alternativaA": "El lobo",
  "alternativaB": "Caperucita",
  "alternativaC": "El cazador",
  "alternativaD": "La abuela",
  "correcta": 1,
  "esPruebaFinal": false
}
```

`correcta` es la posición de la alternativa: `0` = A, `1` = B, `2` = C, `3` = D. Para una pregunta de la prueba final, repite el cuerpo con `"esPruebaFinal": true`.

**Cambiar estado** (`PATCH /unidades/1/estado`, token UTP). Envíalo dos veces, primero con `EN_REVISION` y después con `AUTORIZADA`:

```json
{ "estado": "EN_REVISION" }
```

Para pasar a `EN_REVISION` la unidad necesita al menos un desafío y una pregunta de la prueba final.

**Asignar al curso** (`POST /asignaciones`, token DOCENTE). Las fechas van en milisegundos y deben cubrir la fecha actual:

```json
{
  "unidadId": 1,
  "curso": "2° Básico A",
  "fechaInicio": 1764201600000,
  "fechaFin": 1798675200000,
  "maxIntentos": 3,
  "notificarCurso": true,
  "permitirVerRespuestas": true
}
```

**Resolver desafío** (`POST /desafios/1/resolver`, token ESTUDIANTE). El `1` es el id de la pregunta:

```json
{ "elegida": 1 }
```

**Entregar la prueba** (`POST /pruebas/1/intentos`, token ESTUDIANTE). El `1` es el id de la asignación y `preguntaId` es el id de la pregunta de la prueba final:

```json
{ "respuestas": [ { "preguntaId": 2, "elegida": 0 } ] }
```

### 7.6 Demostración de seguridad (para la presentación)

1. `GET /unidades` **sin** token responde `401 Unauthorized`.
2. La misma petición **con** el token de UTP responde `200`.
3. `POST /unidades` con el token de la estudiante responde `403 Forbidden`, porque solo la UTP crea unidades.
4. Pega el token en [jwt.io](https://jwt.io) para ver su contenido: `sub`, `uid`, `role`, `curso` (solo estudiantes) y la fecha de vencimiento.

## 8. Autenticación y roles

El `api-gateway` valida el token, revisa el rol según la ruta y reenvía a cada servicio los encabezados `X-User-Id`, `X-User-Role` y `X-User-Curso`. Los que envíe el cliente se descartan.

| Ruta | Quién puede |
|---|---|
| `POST /auth/login`, `POST /auth/register` | Público |
| `GET /auth/estudiantes` | DOCENTE, UTP |
| `GET /unidades/**` | Cualquier usuario autenticado |
| `POST`, `PUT`, `PATCH`, `DELETE /unidades/**` | UTP |
| `POST /asignaciones`, `DELETE /asignaciones/{id}`, `GET /asignaciones/docente` | DOCENTE |
| `GET /asignaciones/mias` | ESTUDIANTE |
| `/desafios/**`, `/pruebas/**` | ESTUDIANTE |
| `/informes/**`, `/rendimiento/**` | DOCENTE, UTP |
| `POST /observaciones` | DOCENTE |
| `GET /observaciones` | Cualquier usuario autenticado |
| `/internal/**` | Nadie desde fuera (bloqueado) |

Los servicios se consultan entre sí por rutas `/internal/**`, que el gateway bloquea para el exterior.

## 9. Solución de problemas

### Códigos de respuesta frecuentes

| Respuesta | Causa probable |
|---|---|
| `400` | Falta un campo o no cumple la validación (por ejemplo `titulo` con menos de 5 letras). Un login sin `username` también da 400. |
| `401` | Falta el token, venció, o está mal pegado. Haz login de nuevo. |
| `403` | Estás usando el token de otro rol (por ejemplo el de la estudiante para crear una unidad). |
| `404` | El id no existe. |
| `409` | No se cumple una regla (unidad no autorizada, intentos agotados, usuario ya existente). El mensaje viene en la respuesta. |
| `500` | Error interno del servicio. Revisa los logs (ver abajo). |
| `502` / `503` | Un servicio está caído. Revisa `docker compose ps -a`. |

### Casos que ya se presentaron

**`401` en `/auth/register` o `/auth/login`.** Revisa que el método sea **POST**. Un `GET` a esas rutas se trata como petición protegida y responde 401.

**`401` aunque ya tengas el token.** Al copiar el token desde la respuesta JSON de Postman se pueden copiar las **comillas dobles** (`"eyJhbGci..."`). Postman las envía como parte del valor y el servidor no puede leerlo. El token debe empezar directamente con `eyJhbGci` y terminar sin comillas. Revisa también que en **Headers** no haya un `Authorization` escrito a mano que pise al de la pestaña Authorization.

**`500` sin mensaje al crear una unidad (`POST /unidades`).** Es un error interno, no de autenticación (si llegas al 500, el token funcionó). La causa se ve en los logs:

```powershell
docker compose logs unidad-service --tail 60
```

Una causa conocida es el nombre de la columna `codigo_oa`: Spring convierte el campo `codigoOA` en `codigo_o_a`, pero la tabla usa `codigo_oa`, y MySQL responde `Unknown column`. Se corrige indicando el nombre exacto en las entidades:

`unidad-service/.../model/Unidad.java` y `evaluacion-service/.../model/IntentoPrueba.java`:

```java
@Column(name = "codigo_oa")
private String codigoOA;
```

Luego reconstruye:

```powershell
docker compose up -d --build unidad-service evaluacion-service
```

**Los servicios se detienen al arrancar.** Casi siempre es la conexión a MySQL. Revisa los logs de uno de ellos:

| Lo que ves en los logs | Causa y solución |
|---|---|
| `Communications link failure` o `Connection refused` | MySQL apagado en XAMPP, o `DB_URL` mal escrita |
| `Host '...' is not allowed to connect` | Falta crear `root@'%'` (ver [5.2](#52-permitir-que-docker-se-conecte-root)) |
| `Access denied for user 'root'` | `root@'%'` tiene contraseña distinta a `DB_PASS` (ver nota en 5.2) |
| `Unknown database` | El nombre de la base no coincide |
| `Table ... doesn't exist` o `Unknown column` | La tabla no coincide con la entidad JPA |
| `NullPointerException` | Un dato llega nulo en el controller |

**PowerShell dice que `docker` no se reconoce.** Ejecuta `$env:Path += ";$env:LOCALAPPDATA\Programs\DockerDesktop\resources\bin"` (ver [6.1](#61-verificar-docker)).

**Aviso en VS Code: *"is a non-project file, only syntax errors are reported"*.** No es un error del código. VS Code no detecta los proyectos Maven porque no hay un `pom.xml` en la raíz. Puedes ignorarlo (el build lo hace Docker) o ejecutar **Java: Import Java Projects into Workspace** desde `Ctrl + Shift + P`.

## 10. Ejecución sin Docker

Cada servicio también puede ejecutarse directamente desde IntelliJ o VS Code. En ese caso usan `localhost` por defecto, con MySQL encendido en el puerto 3306. Orden sugerido:

1. `unidad-service`
2. `asignacion-service`
3. `evaluacion-service`
4. `auth-service`
5. `api-gateway`

Para generar el jar de un servicio a mano:

```bash
./mvnw clean package -DskipTests
```

---

<details>
<summary>Crear los proyectos desde cero (Spring Initializr)</summary>

Cada carpeta se puede crear en [start.spring.io](https://start.spring.io) con: Maven, Java, Spring Boot 4.0.10, Jar, Properties, Java 21 y Group `com.example`. Después se copian encima el `src/` y el `Dockerfile` de este proyecto.

Dependencias del Initializr:

- **api-gateway:** Reactive Gateway, Spring Security, OAuth2 Resource Server.
- **auth-service:** Spring Web, Spring Data JPA, MySQL Driver, Lombok, Validation, Liquibase Migration.
- **unidad-service, asignacion-service y evaluacion-service:** Spring Web, Spring Data JPA, MySQL Driver, Liquibase Migration, Lombok, Validation, Spring Boot Actuator.

Solo en `auth-service`, agregar al `pom.xml` (dentro de `dependencies`):

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

</details>
Los servicios se consultan entre sí por rutas /internal/**, que el gateway bloquea para el exterior.
