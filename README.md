<<<<<<< HEAD
# MPAZ-Backend

Backend de **MPAZ PRO 2º** con arquitectura de microservicios. Se entra siempre por el `api-gateway`, que valida el token JWT y autoriza por rol (**UTP**, **DOCENTE** y **ESTUDIANTE**).

Todo corre con **Docker**: los cinco servicios y la base de datos MySQL se levantan con un solo comando. No necesitas instalar Java, Maven, MySQL ni XAMPP.
=======
MPAZ-Backend
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5

## Contenido

<<<<<<< HEAD
1. [Microservicios](#1-microservicios)
2. [Tecnologías](#2-tecnologías)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Requisitos](#4-requisitos)
5. [Inicio rápido](#5-inicio-rápido)
6. [Variables del archivo .env](#6-variables-del-archivo-env)
7. [Comandos de Docker](#7-comandos-de-docker)
8. [Probar la API](#8-probar-la-api)
9. [Autenticación y roles](#9-autenticación-y-roles)
10. [Ver la base de datos](#10-ver-la-base-de-datos)
11. [Solución de problemas](#11-solución-de-problemas)
12. [Ejecución sin Docker](#12-ejecución-sin-docker)

---

## 1. Microservicios

```
Cliente ──► api-gateway :9090 ──┬─► auth-service        :9094 ─► bd_auth
 (Postman,   (valida JWT y rol) ├─► unidad-service      :9091 ─► bd_unidad
  app móvil)                    ├─► asignacion-service  :9092 ─► bd_asignacion
                                └─► evaluacion-service  :9093 ─► bd_evaluacion
                                                    todas las bases viven en el contenedor mysql
```

| Servicio | Puerto | Base de datos | Función |
|---|---|---|---|
| `api-gateway` | 9090 | — | Punto de entrada, validación del token y permisos por rol |
| `auth-service` | 9094 | `bd_auth` | Registro, login y emisión del JWT |
| `unidad-service` | 9091 | `bd_unidad` | Unidades, desafíos, preguntas de la prueba y estados de autorización |
| `asignacion-service` | 9092 | `bd_asignacion` | Asignación de unidades a cursos por el docente |
| `evaluacion-service` | 9093 | `bd_evaluacion` | Avance, intentos, notas, observaciones e informes |
| `mysql` | — | las 4 anteriores | MySQL 8.4 en contenedor, con volumen para que los datos persistan |

Solo el `api-gateway` (9090) queda expuesto a tu PC. Los demás servicios y MySQL se comunican dentro de la red de Docker. Cada servicio tiene su propio README con el detalle de sus endpoints.

## 2. Tecnologías

- Java 21
- Spring Boot 4.0.10
- Spring Web / Spring Cloud Gateway
- Spring Data JPA
- MySQL 8.4
- Liquibase
- Maven
- Docker y Docker Compose
- JWT (jjwt 0.12.5)

## 3. Estructura del proyecto

```
MPAZ-Backend-main/                 ← raíz del repositorio
├── .gitignore                     ← ignora .env, .idea, target, etc.
├── .gitattributes                 ← obliga a que los .sh usen saltos de línea LF
├── README.md
└── MPAZ-Backend/
    ├── .env.example               ← plantilla de variables (se sube a Git)
    ├── .env                       ← tus claves reales (NO se sube a Git)
    ├── docker-compose.yml
    ├── probar-api.ps1             ← prueba completa de la API en un solo script
    ├── mysql/
    │   └── 01-init.sh             ← crea las 4 bases y los permisos la primera vez
    ├── api-gateway/
    ├── auth-service/
    ├── unidad-service/
    ├── asignacion-service/
    └── evaluacion-service/
```

Cada servicio incluye su `pom.xml`, su `src/` y un `Dockerfile` de **dos etapas**: compila con Maven dentro de Docker y luego ejecuta el `.jar`. Por eso **no hace falta** compilar nada antes de levantar.

## 4. Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) con el motor encendido (debe decir *Engine running*)
- [Git](https://git-scm.com/)
- [Postman](https://www.postman.com/) (opcional: también puedes probar con el script `probar-api.ps1`)
- VS Code (opcional, para editar el código)

## 5. Inicio rápido

Todos los comandos son para PowerShell.

**Paso 1. Clonar el repositorio**

```powershell
git clone https://github.com/TU_USUARIO/MPAZ-Backend.git MPAZ-Backend-main
cd MPAZ-Backend-main\MPAZ-Backend
```

La carpeta donde debes estar es la que contiene el `docker-compose.yml`.

**Paso 2. Crear tu archivo `.env`**

El archivo debe llamarse exactamente `.env` y quedar en la misma carpeta que el `docker-compose.yml`. Hay dos formas de crearlo. Usa los valores de ejemplo solo para probar y cambia las claves por las tuyas (ver la [sección 6](#6-variables-del-archivo-env)).

**Opción A: escribirlo directo con PowerShell** (la más segura, evita errores de nombre):

```powershell
=======
Todo corre con Docker: los cinco servicios y la base de datos MySQL se levantan con un solo comando. No necesitas instalar Java, Maven, MySQL ni XAMPP.

Contenido
Microservicios
Tecnologías
Estructura del proyecto
Requisitos
Inicio rápido
Variables del archivo .env
Comandos de Docker
Probar la API
Autenticación y roles
Ver la base de datos
Solución de problemas
Ejecución sin Docker
1. Microservicios
Cliente ──► api-gateway :9090 ──┬─► auth-service        :9094 ─► bd_auth
 (Postman,   (valida JWT y rol) ├─► unidad-service      :9091 ─► bd_unidad
  app móvil)                    ├─► asignacion-service  :9092 ─► bd_asignacion
                                └─► evaluacion-service  :9093 ─► bd_evaluacion
                                                    todas las bases viven en el contenedor mysql
Servicio	Puerto	Base de datos	Función
api-gateway	9090	—	Punto de entrada, validación del token y permisos por rol
auth-service	9094	bd_auth	Registro, login y emisión del JWT
unidad-service	9091	bd_unidad	Unidades, desafíos, preguntas de la prueba y estados de autorización
asignacion-service	9092	bd_asignacion	Asignación de unidades a cursos por el docente
evaluacion-service	9093	bd_evaluacion	Avance, intentos, notas, observaciones e informes
mysql	—	las 4 anteriores	MySQL 8.4 en contenedor, con volumen para que los datos persistan

Solo el api-gateway (9090) queda expuesto a tu PC. Los demás servicios y MySQL se comunican dentro de la red de Docker. Cada servicio tiene su propio README con el detalle de sus endpoints.

2. Tecnologías
Java 21
Spring Boot 4.0.10
Spring Web / Spring Cloud Gateway
Spring Data JPA
MySQL 8.4
Liquibase
Maven
Docker y Docker Compose
JWT (jjwt 0.12.5)
3. Estructura del proyecto
MPAZ-Backend-main/                 ← raíz del repositorio
├── .gitignore                     ← ignora .env, .idea, target, etc.
├── .gitattributes                 ← obliga a que los .sh usen saltos de línea LF
├── README.md
└── MPAZ-Backend/
    ├── .env.example               ← plantilla de variables (se sube a Git)
    ├── .env                       ← tus claves reales (NO se sube a Git)
    ├── docker-compose.yml
    ├── probar-api.ps1             ← prueba completa de la API en un solo script
    ├── mysql/
    │   └── 01-init.sh             ← crea las 4 bases y los permisos la primera vez
    ├── api-gateway/
    ├── auth-service/
    ├── unidad-service/
    ├── asignacion-service/
    └── evaluacion-service/

Cada servicio incluye su pom.xml, su src/ y un Dockerfile de dos etapas: compila con Maven dentro de Docker y luego ejecuta el .jar. Por eso no hace falta compilar nada antes de levantar.

4. Requisitos
Docker Desktop con el motor encendido (debe decir Engine running)
Git
Postman (opcional: también puedes probar con el script probar-api.ps1)
VS Code (opcional, para editar el código)
5. Inicio rápido

Todos los comandos son para PowerShell.

Paso 1. Clonar el repositorio

powershell
git clone https://github.com/TU_USUARIO/MPAZ-Backend.git MPAZ-Backend-main
cd MPAZ-Backend-main\MPAZ-Backend

La carpeta donde debes estar es la que contiene el docker-compose.yml.

Paso 2. Crear tu archivo .env

El archivo debe llamarse exactamente .env y quedar en la misma carpeta que el docker-compose.yml. Hay dos formas de crearlo. Usa los valores de ejemplo solo para probar y cambia las claves por las tuyas (ver la sección 6).

Opción A: escribirlo directo con PowerShell (la más segura, evita errores de nombre):

powershell
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
@"
MYSQL_USER=mpaz
MYSQL_PASSWORD=MiClave_Segura_2026
MYSQL_ROOT_PASSWORD=OtraClave_Root_2026
SECRET=clave-larga-para-firmar-los-jwt-2026
"@ | Set-Content -Path .env -Encoding ascii
<<<<<<< HEAD
```

Si ya existía un `.env`, esto lo reemplaza.

**Opción B: copiar la plantilla y editarla**

```powershell
Copy-Item .env.example .env
notepad .env
```

Pega o cambia los valores y guarda. En el Bloc de notas, cuida que no lo guarde como `.env.txt`.

**Comprobar que quedó bien**

```powershell
Get-Content .env
Get-ChildItem -Force | Where-Object Name -like ".env*"
```

Debes ver las 4 líneas del contenido, y en la lista deben aparecer `.env` y `.env.example` (nunca `.env.txt`).

**Paso 3. Verificar Docker**

```powershell
docker version
```

Debe mostrar **Client** y **Server**. Si dice que no reconoce `docker`, mira la [sección 11](#11-solución-de-problemas).

**Paso 4. Construir y levantar todo**

```powershell
docker compose up -d --build
```

La primera vez tarda varios minutos: Docker descarga MySQL y Maven, y compila los cinco servicios. MySQL tarda unos 20 a 30 segundos en quedar listo y los servicios esperan a que lo esté.

**Paso 5. Comprobar el estado**

```powershell
docker compose ps -a
```

Los seis contenedores (`mysql` y los cinco servicios) deben aparecer como `running` o `Up`, y `mysql` como `healthy`. Si alguno está en `Exited`, mira por qué:

```powershell
docker compose logs unidad-service --tail 60
```

Busca las líneas con `ERROR` y el primer `Caused by:`.

**Paso 6. Probar que funciona**

```powershell
.\probar-api.ps1
```

Si termina con el mensaje verde *"Listo..."*, el backend funciona de punta a punta. El detalle está en la [sección 8](#8-probar-la-api).

## 6. Variables del archivo .env

El `.env` guarda los datos secretos. **No se sube a Git** (lo ignora el `.gitignore`); lo que se sube es el `.env.example`, que tiene los mismos nombres con valores falsos.

| Variable | Para qué sirve |
|---|---|
| `MYSQL_USER` | Usuario de MySQL que usan los microservicios. Solo tiene permisos sobre las 4 bases `bd_*` |
| `MYSQL_PASSWORD` | Contraseña de ese usuario |
| `MYSQL_ROOT_PASSWORD` | Contraseña del usuario `root` del contenedor MySQL |
| `SECRET` | Clave con la que se firman y validan los tokens JWT. Debe ser larga (entre 32 y 47 caracteres). Es la misma para `auth-service` y `api-gateway`; el compose se la entrega a ambos |

**Reglas de formato**

- Sin espacios alrededor del `=`: escribe `SECRET=abc`, no `SECRET = abc`.
- Sin comillas y sin espacios al final de la línea.
- Evita `$`, `#` y espacios dentro de las contraseñas, porque Docker Compose los interpreta de forma especial.
- `SECRET` debe tener entre 32 y 47 caracteres.
- Los valores del ejemplo son públicos (están en este README). Para tu proyecto, cámbialos por los tuyos.

Si falta alguna variable, `docker compose` se detiene con un mensaje como *"Configura SECRET en el archivo .env"*.

> Si cambias `MYSQL_USER`, `MYSQL_PASSWORD` o `MYSQL_ROOT_PASSWORD` **después** de haber levantado el proyecto, MySQL sigue usando los valores antiguos porque quedaron guardados en el volumen. Para aplicarlos hay que borrar el volumen (ver `down -v` en la sección 7). **Eso borra todos los datos.**

## 7. Comandos de Docker

Ejecútalos desde la carpeta del `docker-compose.yml`.

```powershell
=======

Si ya existía un .env, esto lo reemplaza.

Opción B: copiar la plantilla y editarla

powershell
Copy-Item .env.example .env
notepad .env

Pega o cambia los valores y guarda. En el Bloc de notas, cuida que no lo guarde como .env.txt.

Comprobar que quedó bien

powershell
Get-Content .env
Get-ChildItem -Force | Where-Object Name -like ".env*"

Debes ver las 4 líneas del contenido, y en la lista deben aparecer .env y .env.example (nunca .env.txt).

Paso 3. Verificar Docker

powershell
docker version

Debe mostrar Client y Server. Si dice que no reconoce docker, mira la sección 11.

Paso 4. Construir y levantar todo

powershell
docker compose up -d --build

La primera vez tarda varios minutos: Docker descarga MySQL y Maven, y compila los cinco servicios. MySQL tarda unos 20 a 30 segundos en quedar listo y los servicios esperan a que lo esté.

Paso 5. Comprobar el estado

powershell
docker compose ps -a

Los seis contenedores (mysql y los cinco servicios) deben aparecer como running o Up, y mysql como healthy. Si alguno está en Exited, mira por qué:

powershell
docker compose logs unidad-service --tail 60

Busca las líneas con ERROR y el primer Caused by:.

Paso 6. Probar que funciona

powershell
.\probar-api.ps1

Si termina con el mensaje verde "Listo...", el backend funciona de punta a punta. El detalle está en la sección 8.

6. Variables del archivo .env

El .env guarda los datos secretos. No se sube a Git (lo ignora el .gitignore); lo que se sube es el .env.example, que tiene los mismos nombres con valores falsos.

Variable	Para qué sirve
MYSQL_USER	Usuario de MySQL que usan los microservicios. Solo tiene permisos sobre las 4 bases bd_*
MYSQL_PASSWORD	Contraseña de ese usuario
MYSQL_ROOT_PASSWORD	Contraseña del usuario root del contenedor MySQL
SECRET	Clave con la que se firman y validan los tokens JWT. Debe ser larga (entre 32 y 47 caracteres). Es la misma para auth-service y api-gateway; el compose se la entrega a ambos

Reglas de formato

Sin espacios alrededor del =: escribe SECRET=abc, no SECRET = abc.
Sin comillas y sin espacios al final de la línea.
Evita $, # y espacios dentro de las contraseñas, porque Docker Compose los interpreta de forma especial.
SECRET debe tener entre 32 y 47 caracteres.
Los valores del ejemplo son públicos (están en este README). Para tu proyecto, cámbialos por los tuyos.

Si falta alguna variable, docker compose se detiene con un mensaje como "Configura SECRET en el archivo .env".

Si cambias MYSQL_USER, MYSQL_PASSWORD o MYSQL_ROOT_PASSWORD después de haber levantado el proyecto, MySQL sigue usando los valores antiguos porque quedaron guardados en el volumen. Para aplicarlos hay que borrar el volumen (ver down -v en la sección 7). Eso borra todos los datos.

7. Comandos de Docker

Ejecútalos desde la carpeta del docker-compose.yml.

powershell
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
docker compose up -d --build          # construir y levantar todo
docker compose ps -a                  # ver el estado de los contenedores
docker compose logs -f api-gateway    # ver los logs de un servicio en vivo (Ctrl+C para salir)
docker compose logs mysql --tail 60   # últimas 60 líneas de MySQL
docker compose restart auth-service   # reiniciar un servicio
docker compose down                   # apagar y eliminar los contenedores (los datos se conservan)
docker compose down -v                # apagar y BORRAR también la base de datos (volumen)
<<<<<<< HEAD
```

**Reconstruir un servicio después de cambiar código.** Los contenedores siguen ejecutando el código anterior hasta que se reconstruyen:

```powershell
docker compose up -d --build unidad-service evaluacion-service
```

**Empezar de cero** (borra usuarios, unidades y todo lo guardado):

```powershell
docker compose down -v
docker compose up -d --build
```

## 8. Probar la API

Todas las peticiones van al gateway: `http://localhost:9090`.

### 8.1 Opción rápida: script automático

```powershell
.\probar-api.ps1
```

Si PowerShell bloquea la ejecución de scripts:

```powershell
powershell -ExecutionPolicy Bypass -File .\probar-api.ps1
```

El script hace todo el flujo de la [sección 8.4](#84-flujo-completo-de-prueba): registra los tres usuarios, hace login, crea y autoriza una unidad, la asigna a un curso, resuelve un desafío, entrega la prueba, consulta informes y termina con la demostración de seguridad. Se puede ejecutar varias veces: reutiliza los usuarios y crea una unidad nueva en cada corrida.

### 8.2 Orden recomendado (Postman)

| Paso | Petición | ¿Lleva token? |
|---|---|---|
| 1 | `POST /auth/register` (crear usuario) | No |
| 2 | `POST /auth/login` (aquí se obtiene el token) | No |
| 3 | Todo lo demás | Sí (Bearer) |

**El token lo entrega el login, no el registro.** Primero se crea el usuario y después se hace login.

### 8.3 Registrar los tres usuarios

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

Cada uno responde `201 Created` con su `id`. Si responde `409`, el usuario ya existe. El estudiante **debe** llevar `curso`.

Ejemplo en PowerShell, sin Postman:

```powershell
Invoke-RestMethod -Method POST -Uri http://localhost:9090/auth/register `
  -ContentType "application/json" `
  -Body '{"nombre":"Ana UTP","username":"utp1","password":"1234","role":"UTP"}'
```

**Iniciar sesión.** `POST http://localhost:9090/auth/login`:

```json
{ "username": "utp1", "password": "1234" }
```

La respuesta trae el campo `token` (además de `id`, `nombre`, `username`, `role` y `curso`). Cópialo **sin las comillas**. Ejemplo en PowerShell, guardando el token en una variable:

```powershell
=======

Reconstruir un servicio después de cambiar código. Los contenedores siguen ejecutando el código anterior hasta que se reconstruyen:

powershell
docker compose up -d --build unidad-service evaluacion-service

Empezar de cero (borra usuarios, unidades y todo lo guardado):

powershell
docker compose down -v
docker compose up -d --build
8. Probar la API

Todas las peticiones van al gateway: http://localhost:9090.

8.1 Opción rápida: script automático
powershell
.\probar-api.ps1

Si PowerShell bloquea la ejecución de scripts:

powershell
powershell -ExecutionPolicy Bypass -File .\probar-api.ps1

El script hace todo el flujo de la sección 8.4: registra los tres usuarios, hace login, crea y autoriza una unidad, la asigna a un curso, resuelve un desafío, entrega la prueba, consulta informes y termina con la demostración de seguridad. Se puede ejecutar varias veces: reutiliza los usuarios y crea una unidad nueva en cada corrida.

8.2 Orden recomendado (Postman)
Paso	Petición	¿Lleva token?
1	POST /auth/register (crear usuario)	No
2	POST /auth/login (aquí se obtiene el token)	No
3	Todo lo demás	Sí (Bearer)

El token lo entrega el login, no el registro. Primero se crea el usuario y después se hace login.

8.3 Registrar los tres usuarios

POST http://localhost:9090/auth/register, en Body → raw → JSON. Envía uno por uno:

json
{ "nombre": "Ana UTP", "username": "utp1", "password": "1234", "role": "UTP" }
json
{ "nombre": "Beto Docente", "username": "doc1", "password": "1234", "role": "DOCENTE" }
json
{ "nombre": "Caro Alumna", "username": "est1", "password": "1234", "role": "ESTUDIANTE", "curso": "2° Básico A" }

Cada uno responde 201 Created con su id. Si responde 409, el usuario ya existe. El estudiante debe llevar curso.

Ejemplo en PowerShell, sin Postman:

powershell
Invoke-RestMethod -Method POST -Uri http://localhost:9090/auth/register `
  -ContentType "application/json" `
  -Body '{"nombre":"Ana UTP","username":"utp1","password":"1234","role":"UTP"}'

Iniciar sesión. POST http://localhost:9090/auth/login:

json
{ "username": "utp1", "password": "1234" }

La respuesta trae el campo token (además de id, nombre, username, role y curso). Cópialo sin las comillas. Ejemplo en PowerShell, guardando el token en una variable:

powershell
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
$login = Invoke-RestMethod -Method POST -Uri http://localhost:9090/auth/login `
  -ContentType "application/json" `
  -Body '{"username":"utp1","password":"1234"}'
$tokenUtp = $login.token
<<<<<<< HEAD
```

**Usar el token.** En cada petición que no sea login ni registro:

1. Abre la pestaña **Authorization**.
2. En **Auth Type** elige **Bearer Token**.
3. Pega el token en el campo **Token** (sin comillas y sin escribir `Bearer`; Postman lo agrega).

Equivale al encabezado:

```http
Authorization: Bearer TU_TOKEN
```

Cada rol tiene su propio token. Haz login con `utp1`, `doc1` y `est1` y usa el que corresponda a cada paso. El token dura 4 horas.

**Atajo opcional en Postman:** en la pestaña **Tests** de la petición de login, pega esto para guardar cada token automáticamente:

```javascript
const data = pm.response.json();
pm.collectionVariables.set("token_" + data.role.toLowerCase(), data.token);
```

Después, en **Authorization** de las demás peticiones, escribe `{{token_utp}}`, `{{token_docente}}` o `{{token_estudiante}}` según el rol.

### 8.4 Flujo completo de prueba

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
| Ver mis intentos | ESTUDIANTE | `GET /pruebas/1/mis-intentos` |
| Ver informes y rendimiento | DOCENTE o UTP | `GET /informes/...`, `GET /rendimiento` |
| Dejar una observación | DOCENTE | `POST /observaciones` |

Los números `1` de las URLs son ejemplos: usa el `id` que te devolvió cada respuesta anterior.

**Crear unidad** (`POST /unidades`, token UTP):

```json
=======

Usar el token. En cada petición que no sea login ni registro:

Abre la pestaña Authorization.
En Auth Type elige Bearer Token.
Pega el token en el campo Token (sin comillas y sin escribir Bearer; Postman lo agrega).

Equivale al encabezado:

http
Authorization: Bearer TU_TOKEN

Cada rol tiene su propio token. Haz login con utp1, doc1 y est1 y usa el que corresponda a cada paso. El token dura 4 horas.

Atajo opcional en Postman: en la pestaña Tests de la petición de login, pega esto para guardar cada token automáticamente:

javascript
const data = pm.response.json();
pm.collectionVariables.set("token_" + data.role.toLowerCase(), data.token);

Después, en Authorization de las demás peticiones, escribe {{token_utp}}, {{token_docente}} o {{token_estudiante}} según el rol.

8.4 Flujo completo de prueba
Paso	Con el token de	Método y URL
Crear unidad	UTP	POST /unidades
Crear desafío y pregunta de prueba final	UTP	POST /unidades/1/preguntas (dos veces)
Enviar a revisión	UTP	PATCH /unidades/1/estado
Autorizar	UTP	PATCH /unidades/1/estado
Asignar al curso	DOCENTE	POST /asignaciones
Ver mis asignaciones	ESTUDIANTE	GET /asignaciones/mias
Resolver desafío	ESTUDIANTE	POST /desafios/1/resolver
Entregar prueba	ESTUDIANTE	POST /pruebas/1/intentos
Ver mis intentos	ESTUDIANTE	GET /pruebas/1/mis-intentos
Ver informes y rendimiento	DOCENTE o UTP	GET /informes/..., GET /rendimiento
Dejar una observación	DOCENTE	POST /observaciones

Los números 1 de las URLs son ejemplos: usa el id que te devolvió cada respuesta anterior.

Crear unidad (POST /unidades, token UTP):

json
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
{
  "asignatura": "Lenguaje",
  "titulo": "Los cuentos",
  "codigoOA": "OA 3",
  "objetivo": "Leer cuentos breves",
  "explicacion": "Un cuento tiene inicio, desarrollo y final.",
  "ejemplo": "Caperucita Roja"
}
<<<<<<< HEAD
```

Responde `201` con la unidad en estado `BORRADOR`. El `titulo` debe tener entre 5 y 60 caracteres. Opcionalmente se pueden enviar `instrucciones` e `imagenUri`.

**Crear desafío** (`POST /unidades/1/preguntas`, token UTP):

```json
=======

Responde 201 con la unidad en estado BORRADOR. El titulo debe tener entre 5 y 60 caracteres. Opcionalmente se pueden enviar instrucciones e imagenUri.

Crear desafío (POST /unidades/1/preguntas, token UTP):

json
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
{
  "texto": "¿Quién entra al bosque?",
  "alternativaA": "El lobo",
  "alternativaB": "Caperucita",
  "alternativaC": "El cazador",
  "alternativaD": "La abuela",
  "correcta": 1,
  "esPruebaFinal": false
}
<<<<<<< HEAD
```

`correcta` es la posición de la alternativa: `0` = A, `1` = B, `2` = C, `3` = D.

**Crear pregunta de la prueba final** (misma URL, token UTP). Es el mismo cuerpo con `"esPruebaFinal": true`:

```json
=======

correcta es la posición de la alternativa: 0 = A, 1 = B, 2 = C, 3 = D.

Crear pregunta de la prueba final (misma URL, token UTP). Es el mismo cuerpo con "esPruebaFinal": true:

json
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
{
  "texto": "¿Qué parte del cuento presenta a los personajes?",
  "alternativaA": "El inicio",
  "alternativaB": "El desarrollo",
  "alternativaC": "El final",
  "alternativaD": "El título",
  "correcta": 0,
  "esPruebaFinal": true
}
<<<<<<< HEAD
```

**Cambiar estado** (`PATCH /unidades/1/estado`, token UTP). Envíalo dos veces, primero con `EN_REVISION` y después con `AUTORIZADA`:

```json
{ "estado": "EN_REVISION" }
```

```json
{ "estado": "AUTORIZADA" }
```

Para pasar a `EN_REVISION` la unidad necesita al menos un desafío y una pregunta de la prueba final.

**Asignar al curso** (`POST /asignaciones`, token DOCENTE). Las fechas van en milisegundos desde 1970 (Unix) y deben cubrir la fecha actual. El `curso` debe ser igual al del estudiante:

```json
=======

Cambiar estado (PATCH /unidades/1/estado, token UTP). Envíalo dos veces, primero con EN_REVISION y después con AUTORIZADA:

json
{ "estado": "EN_REVISION" }
json
{ "estado": "AUTORIZADA" }

Para pasar a EN_REVISION la unidad necesita al menos un desafío y una pregunta de la prueba final.

Asignar al curso (POST /asignaciones, token DOCENTE). Las fechas van en milisegundos desde 1970 (Unix) y deben cubrir la fecha actual. El curso debe ser igual al del estudiante:

json
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
{
  "unidadId": 1,
  "curso": "2° Básico A",
  "fechaInicio": 1764201600000,
  "fechaFin": 1798675200000,
  "maxIntentos": 3,
  "notificarCurso": true,
  "permitirVerRespuestas": true
}
<<<<<<< HEAD
```

Para calcular las fechas de hoy en PowerShell:

```powershell
[DateTimeOffset]::UtcNow.AddDays(-1).ToUnixTimeMilliseconds()    # fechaInicio
[DateTimeOffset]::UtcNow.AddDays(365).ToUnixTimeMilliseconds()   # fechaFin
```

**Resolver desafío** (`POST /desafios/1/resolver`, token ESTUDIANTE). El `1` es el id de la pregunta:

```json
{ "elegida": 1 }
```

**Entregar la prueba** (`POST /pruebas/1/intentos`, token ESTUDIANTE). El `1` de la URL es el id de la **asignación**, y `preguntaId` es el id de la pregunta de la prueba final:

```json
{ "respuestas": [ { "preguntaId": 2, "elegida": 0 } ] }
```

**Informes y rendimiento** (token DOCENTE o UTP):

| Petición | Qué devuelve |
|---|---|
| `GET /informes/asignacion/1` | Informe de una asignación (id de la asignación) |
| `GET /informes/estudiante/3/unidad/1` | Informe de un estudiante en una unidad (id del estudiante y de la unidad) |
| `GET /rendimiento` | Rendimiento por objetivo de aprendizaje. Acepta `?curso=...` |
| `GET /rendimiento/cursos` | Rendimiento por curso |

**Observación del docente** (`POST /observaciones`, token DOCENTE, texto de hasta 500 caracteres):

```json
{ "estudianteId": 3, "unidadId": 1, "texto": "Buen trabajo en la unidad." }
```

### 8.5 Demostración de seguridad (para la presentación)

1. `GET /unidades` **sin** token responde `401 Unauthorized`.
2. La misma petición **con** el token de UTP responde `200`.
3. `POST /unidades` con el token de la estudiante responde `403 Forbidden`, porque solo la UTP crea unidades.
4. `GET /internal/unidades` responde `403` incluso con token: las rutas `/internal/**` están bloqueadas desde fuera.
5. Pega el token en [jwt.io](https://jwt.io) para ver su contenido: `sub`, `uid`, `role`, `curso` (solo estudiantes) y la fecha de vencimiento.

El script `probar-api.ps1` ejecuta los puntos 1 a 4 al final.

## 9. Autenticación y roles

El `api-gateway` valida el token, revisa el rol según la ruta y reenvía a cada servicio los encabezados `X-User-Id`, `X-User-Role` y `X-User-Curso`. Los que envíe el cliente se descartan.

| Ruta | Quién puede |
|---|---|
| `POST /auth/login`, `POST /auth/register` | Público |
| `GET /auth/estudiantes` | DOCENTE, UTP |
| `GET /unidades/**` | Cualquier usuario autenticado |
| `POST`, `PUT`, `PATCH`, `DELETE /unidades/**` | UTP |
| `POST /asignaciones`, `DELETE /asignaciones/{id}`, `GET /asignaciones/docente` | DOCENTE |
| `GET /asignaciones/mias` | ESTUDIANTE |
| `GET /asignaciones/{id}` | Cualquier usuario autenticado |
| `/desafios/**`, `/pruebas/**` | ESTUDIANTE |
| `/informes/**`, `/rendimiento/**` | DOCENTE, UTP |
| `POST /observaciones` | DOCENTE |
| `GET /observaciones` | Cualquier usuario autenticado |
| `/internal/**` | Nadie desde fuera (bloqueado) |

Los servicios se consultan entre sí por rutas `/internal/**`, que el gateway bloquea para el exterior.

### Lista completa de endpoints

Todas las URLs empiezan con `http://localhost:9090` (el gateway). Los `{...}` se reemplazan por el id correspondiente.

| Método | URL | Quién puede | Para qué sirve |
|---|---|---|---|
| `POST` | `/auth/register` | Público | Crear un usuario |
| `POST` | `/auth/login` | Público | Iniciar sesión y obtener el token |
| `GET` | `/auth/estudiantes` | DOCENTE, UTP | Listar estudiantes |
| `POST` | `/unidades` | UTP | Crear una unidad |
| `GET` | `/unidades` | Autenticado | Listar unidades |
| `GET` | `/unidades/{id}` | Autenticado | Ver una unidad |
| `PUT` | `/unidades/{id}` | UTP | Editar una unidad completa |
| `PATCH` | `/unidades/{id}/estado` | UTP | Cambiar el estado (`EN_REVISION`, `AUTORIZADA`) |
| `DELETE` | `/unidades/{id}` | UTP | Borrar una unidad |
| `POST` | `/unidades/{id}/preguntas` | UTP | Crear un desafío o pregunta de la prueba final |
| `GET` | `/unidades/{id}/preguntas` | Autenticado | Listar las preguntas de una unidad |
| `PUT` | `/unidades/{id}/preguntas/{preguntaId}` | UTP | Editar una pregunta |
| `DELETE` | `/unidades/{id}/preguntas/{preguntaId}` | UTP | Borrar una pregunta |
| `POST` | `/asignaciones` | DOCENTE | Asignar una unidad a un curso |
| `GET` | `/asignaciones/docente` | DOCENTE | Ver las asignaciones del docente |
| `GET` | `/asignaciones/mias` | ESTUDIANTE | Ver las asignaciones de mi curso |
| `GET` | `/asignaciones/{id}` | Autenticado | Ver una asignación |
| `DELETE` | `/asignaciones/{id}` | DOCENTE | Borrar una asignación |
| `POST` | `/desafios/{preguntaId}/resolver` | ESTUDIANTE | Responder un desafío |
| `GET` | `/desafios/unidad/{unidadId}/avance` | ESTUDIANTE | Ver mi avance en una unidad |
| `POST` | `/pruebas/{asignacionId}/intentos` | ESTUDIANTE | Entregar la prueba final |
| `GET` | `/pruebas/{asignacionId}/mis-intentos` | ESTUDIANTE | Ver mis intentos de la prueba |
| `GET` | `/informes/asignacion/{asignacionId}` | DOCENTE, UTP | Informe de una asignación |
| `GET` | `/informes/estudiante/{estudianteId}/unidad/{unidadId}` | DOCENTE, UTP | Informe de un estudiante en una unidad |
| `GET` | `/rendimiento` | DOCENTE, UTP | Rendimiento por objetivo de aprendizaje (`?curso=...` opcional) |
| `GET` | `/rendimiento/cursos` | DOCENTE, UTP | Rendimiento por curso |
| `POST` | `/observaciones` | DOCENTE | Dejar una observación a un estudiante |
| `GET` | `/observaciones` | Autenticado | Ver observaciones |

## 10. Ver la base de datos

MySQL corre dentro de Docker y **no está expuesto** a tu PC, así que no aparece en phpMyAdmin. Dos formas de mirarlo.

**Desde la consola del contenedor** (te pide la contraseña de `MYSQL_ROOT_PASSWORD`):

```powershell
docker compose exec mysql mysql -u root -p
```

Dentro puedes ejecutar:

```sql
SHOW DATABASES;
USE bd_auth;
SELECT id, nombre, username, role, curso FROM usuarios;
```

O en una sola línea:

```powershell
docker compose exec mysql mysql -u root -p -e "SHOW DATABASES;"
```

Deben aparecer `bd_auth`, `bd_unidad`, `bd_asignacion` y `bd_evaluacion`. Las tablas las crea Liquibase al arrancar cada servicio.

**Desde un programa externo (Workbench, DBeaver, etc.).** Agrega estas dos líneas al servicio `mysql` del `docker-compose.yml`:

```yaml
    ports:
      - "3307:3306"
```

Reinicia con `docker compose up -d` y conéctate a `localhost`, puerto `3307`, con el usuario y la contraseña de tu `.env`. Se usa el 3307 para no chocar con un MySQL instalado en tu PC.

## 11. Solución de problemas

### Códigos de respuesta frecuentes

| Respuesta | Causa probable |
|---|---|
| `400` | Falta un campo o no cumple la validación (por ejemplo `titulo` con menos de 5 letras). Un login sin `username` también da 400. Un estudiante sin `curso` también |
| `401` | Falta el token, venció, o está mal pegado. Haz login de nuevo |
| `403` | Estás usando el token de otro rol (por ejemplo el de la estudiante para crear una unidad) |
| `404` | El id no existe |
| `409` | No se cumple una regla (unidad no autorizada, intentos agotados, usuario ya existente). El mensaje viene en la respuesta |
| `500` | Error interno del servicio. Revisa los logs (ver abajo) |
| `502` / `503` | Un servicio está caído. Revisa `docker compose ps -a` |

### Errores al levantar con Docker

| Lo que ves | Causa y solución |
|---|---|
| `Configura SECRET en el archivo .env` o `required variable ... is missing` | Falta el archivo `.env` o una variable. Copia `.env.example` a `.env` y complétalo |
| `bad interpreter` o `/bin/bash^M` en los logs de `mysql` | El script `mysql/01-init.sh` tiene saltos de línea de Windows (CRLF). Ejecuta `git add --renormalize .` y vuelve a clonar, o conviértelo a LF desde VS Code (barra inferior: `CRLF` → `LF`). El `.gitattributes` evita que vuelva a pasar |
| `Access denied for user` | Cambiaste las claves del `.env` después de crear el volumen. Ejecuta `docker compose down -v` y levanta de nuevo (borra los datos) |
| `Unknown database` | El volumen se creó antes de que existiera el script de inicio. Ejecuta `docker compose down -v` y levanta de nuevo |
| `Communications link failure` o `Connection refused` | MySQL aún no termina de arrancar o falló. Revisa `docker compose logs mysql --tail 60` |
| `port is already allocated` (9090) | Otro programa usa el puerto 9090. Ciérralo o cambia `"9090:9090"` en el compose |
| `Table ... doesn't exist` o `Unknown column` | La tabla no coincide con la entidad JPA. Revisa el changelog de Liquibase del servicio |
| Un servicio queda en `Exited` | Mira sus logs: `docker compose logs NOMBRE --tail 60` y busca el primer `Caused by:` |

### Otros casos

**`401` en `/auth/register` o `/auth/login`.** Revisa que el método sea **POST**. Un `GET` a esas rutas se trata como petición protegida y responde 401.

**`401` aunque ya tengas el token.** Al copiar el token desde la respuesta JSON de Postman se pueden copiar las **comillas dobles** (`"eyJhbGci..."`). El token debe empezar directamente con `eyJhbGci` y terminar sin comillas. Revisa también que en **Headers** no haya un `Authorization` escrito a mano que pise al de la pestaña Authorization.

**El token dejó de funcionar tras cambiar `SECRET`.** Los tokens se firman con esa clave. Si la cambias, reinicia el gateway y el auth-service (`docker compose up -d`) y haz login de nuevo.

**PowerShell dice que `docker` no se reconoce.** Agrega su ruta a la sesión actual:

```powershell
$env:Path += ";$env:LOCALAPPDATA\Programs\DockerDesktop\resources\bin"
docker version
```

Si en *Server* dice `failed to connect`, abre Docker Desktop desde el menú Inicio y espera a que diga *Engine running*. Para no repetir esa línea, cierra VS Code y todas las ventanas de PowerShell y abre una nueva.

**Aviso en VS Code: *"is a non-project file, only syntax errors are reported"*.** No es un error del código. VS Code no detecta los proyectos Maven porque no hay un `pom.xml` en la raíz. Puedes ignorarlo (el build lo hace Docker) o ejecutar **Java: Import Java Projects into Workspace** desde `Ctrl + Shift + P`.

## 12. Ejecución sin Docker

Cada servicio también puede ejecutarse directamente desde IntelliJ o VS Code. En ese caso necesitas Java 21 y un MySQL propio (por ejemplo XAMPP) encendido en el puerto 3306. Por defecto los servicios se conectan a `localhost:3306` con el usuario `root` sin contraseña, y la base de datos se crea sola. Si tu MySQL es distinto, define las variables de entorno `DB_URL`, `DB_USER` y `DB_PASS`.

Orden sugerido:

1. `unidad-service`
2. `asignacion-service`
3. `evaluacion-service`
4. `auth-service`
5. `api-gateway`

Para generar el jar de un servicio a mano:

```bash
./mvnw clean package -DskipTests
```

Si ejecutas sin Docker, el gateway y el auth-service usan un `SECRET` por defecto de desarrollo. En Docker se usa siempre el de tu `.env`.

---

<details>
<summary>Crear los proyectos desde cero (Spring Initializr)</summary>

Cada carpeta se puede crear en [start.spring.io](https://start.spring.io) con: Maven, Java, Spring Boot 4.0.10, Jar, Properties, Java 21 y Group `com.example`. Después se copian encima el `src/` y el `Dockerfile` de este proyecto.
=======

Para calcular las fechas de hoy en PowerShell:

powershell
[DateTimeOffset]::UtcNow.AddDays(-1).ToUnixTimeMilliseconds()    # fechaInicio
[DateTimeOffset]::UtcNow.AddDays(365).ToUnixTimeMilliseconds()   # fechaFin

Resolver desafío (POST /desafios/1/resolver, token ESTUDIANTE). El 1 es el id de la pregunta:

json
{ "elegida": 1 }

Entregar la prueba (POST /pruebas/1/intentos, token ESTUDIANTE). El 1 de la URL es el id de la asignación, y preguntaId es el id de la pregunta de la prueba final:

json
{ "respuestas": [ { "preguntaId": 2, "elegida": 0 } ] }

Informes y rendimiento (token DOCENTE o UTP):

Petición	Qué devuelve
GET /informes/asignacion/1	Informe de una asignación (id de la asignación)
GET /informes/estudiante/3/unidad/1	Informe de un estudiante en una unidad (id del estudiante y de la unidad)
GET /rendimiento	Rendimiento por objetivo de aprendizaje. Acepta ?curso=...
GET /rendimiento/cursos	Rendimiento por curso

Observación del docente (POST /observaciones, token DOCENTE, texto de hasta 500 caracteres):

json
{ "estudianteId": 3, "unidadId": 1, "texto": "Buen trabajo en la unidad." }
8.5 Demostración de seguridad (para la presentación)
GET /unidades sin token responde 401 Unauthorized.
La misma petición con el token de UTP responde 200.
POST /unidades con el token de la estudiante responde 403 Forbidden, porque solo la UTP crea unidades.
GET /internal/unidades responde 403 incluso con token: las rutas /internal/** están bloqueadas desde fuera.
Pega el token en jwt.io para ver su contenido: sub, uid, role, curso (solo estudiantes) y la fecha de vencimiento.

El script probar-api.ps1 ejecuta los puntos 1 a 4 al final.

9. Autenticación y roles

El api-gateway valida el token, revisa el rol según la ruta y reenvía a cada servicio los encabezados X-User-Id, X-User-Role y X-User-Curso. Los que envíe el cliente se descartan.

Ruta	Quién puede
POST /auth/login, POST /auth/register	Público
GET /auth/estudiantes	DOCENTE, UTP
GET /unidades/**	Cualquier usuario autenticado
POST, PUT, PATCH, DELETE /unidades/**	UTP
POST /asignaciones, DELETE /asignaciones/{id}, GET /asignaciones/docente	DOCENTE
GET /asignaciones/mias	ESTUDIANTE
GET /asignaciones/{id}	Cualquier usuario autenticado
/desafios/**, /pruebas/**	ESTUDIANTE
/informes/**, /rendimiento/**	DOCENTE, UTP
POST /observaciones	DOCENTE
GET /observaciones	Cualquier usuario autenticado
/internal/**	Nadie desde fuera (bloqueado)

Los servicios se consultan entre sí por rutas /internal/**, que el gateway bloquea para el exterior.

Lista completa de endpoints

Todas las URLs empiezan con http://localhost:9090 (el gateway). Los {...} se reemplazan por el id correspondiente.

Método	URL	Quién puede	Para qué sirve
POST	/auth/register	Público	Crear un usuario
POST	/auth/login	Público	Iniciar sesión y obtener el token
GET	/auth/estudiantes	DOCENTE, UTP	Listar estudiantes
POST	/unidades	UTP	Crear una unidad
GET	/unidades	Autenticado	Listar unidades
GET	/unidades/{id}	Autenticado	Ver una unidad
PUT	/unidades/{id}	UTP	Editar una unidad completa
PATCH	/unidades/{id}/estado	UTP	Cambiar el estado (EN_REVISION, AUTORIZADA)
DELETE	/unidades/{id}	UTP	Borrar una unidad
POST	/unidades/{id}/preguntas	UTP	Crear un desafío o pregunta de la prueba final
GET	/unidades/{id}/preguntas	Autenticado	Listar las preguntas de una unidad
PUT	/unidades/{id}/preguntas/{preguntaId}	UTP	Editar una pregunta
DELETE	/unidades/{id}/preguntas/{preguntaId}	UTP	Borrar una pregunta
POST	/asignaciones	DOCENTE	Asignar una unidad a un curso
GET	/asignaciones/docente	DOCENTE	Ver las asignaciones del docente
GET	/asignaciones/mias	ESTUDIANTE	Ver las asignaciones de mi curso
GET	/asignaciones/{id}	Autenticado	Ver una asignación
DELETE	/asignaciones/{id}	DOCENTE	Borrar una asignación
POST	/desafios/{preguntaId}/resolver	ESTUDIANTE	Responder un desafío
GET	/desafios/unidad/{unidadId}/avance	ESTUDIANTE	Ver mi avance en una unidad
POST	/pruebas/{asignacionId}/intentos	ESTUDIANTE	Entregar la prueba final
GET	/pruebas/{asignacionId}/mis-intentos	ESTUDIANTE	Ver mis intentos de la prueba
GET	/informes/asignacion/{asignacionId}	DOCENTE, UTP	Informe de una asignación
GET	/informes/estudiante/{estudianteId}/unidad/{unidadId}	DOCENTE, UTP	Informe de un estudiante en una unidad
GET	/rendimiento	DOCENTE, UTP	Rendimiento por objetivo de aprendizaje (?curso=... opcional)
GET	/rendimiento/cursos	DOCENTE, UTP	Rendimiento por curso
POST	/observaciones	DOCENTE	Dejar una observación a un estudiante
GET	/observaciones	Autenticado	Ver observaciones
10. Ver la base de datos

MySQL corre dentro de Docker y no está expuesto a tu PC, así que no aparece en phpMyAdmin. Dos formas de mirarlo.

Desde la consola del contenedor (te pide la contraseña de MYSQL_ROOT_PASSWORD):

powershell
docker compose exec mysql mysql -u root -p

Dentro puedes ejecutar:

sql
SHOW DATABASES;
USE bd_auth;
SELECT id, nombre, username, role, curso FROM usuarios;

O en una sola línea:

powershell
docker compose exec mysql mysql -u root -p -e "SHOW DATABASES;"

Deben aparecer bd_auth, bd_unidad, bd_asignacion y bd_evaluacion. Las tablas las crea Liquibase al arrancar cada servicio.

Desde un programa externo (Workbench, DBeaver, etc.). Agrega estas dos líneas al servicio mysql del docker-compose.yml:

yaml
    ports:
      - "3307:3306"

Reinicia con docker compose up -d y conéctate a localhost, puerto 3307, con el usuario y la contraseña de tu .env. Se usa el 3307 para no chocar con un MySQL instalado en tu PC.

11. Solución de problemas
Códigos de respuesta frecuentes
Respuesta	Causa probable
400	Falta un campo o no cumple la validación (por ejemplo titulo con menos de 5 letras). Un login sin username también da 400. Un estudiante sin curso también
401	Falta el token, venció, o está mal pegado. Haz login de nuevo
403	Estás usando el token de otro rol (por ejemplo el de la estudiante para crear una unidad)
404	El id no existe
409	No se cumple una regla (unidad no autorizada, intentos agotados, usuario ya existente). El mensaje viene en la respuesta
500	Error interno del servicio. Revisa los logs (ver abajo)
502 / 503	Un servicio está caído. Revisa docker compose ps -a
Errores al levantar con Docker
Lo que ves	Causa y solución
Configura SECRET en el archivo .env o required variable ... is missing	Falta el archivo .env o una variable. Copia .env.example a .env y complétalo
bad interpreter o /bin/bash^M en los logs de mysql	El script mysql/01-init.sh tiene saltos de línea de Windows (CRLF). Ejecuta git add --renormalize . y vuelve a clonar, o conviértelo a LF desde VS Code (barra inferior: CRLF → LF). El .gitattributes evita que vuelva a pasar
Access denied for user	Cambiaste las claves del .env después de crear el volumen. Ejecuta docker compose down -v y levanta de nuevo (borra los datos)
Unknown database	El volumen se creó antes de que existiera el script de inicio. Ejecuta docker compose down -v y levanta de nuevo
Communications link failure o Connection refused	MySQL aún no termina de arrancar o falló. Revisa docker compose logs mysql --tail 60
port is already allocated (9090)	Otro programa usa el puerto 9090. Ciérralo o cambia "9090:9090" en el compose
Table ... doesn't exist o Unknown column	La tabla no coincide con la entidad JPA. Revisa el changelog de Liquibase del servicio
Un servicio queda en Exited	Mira sus logs: docker compose logs NOMBRE --tail 60 y busca el primer Caused by:
Otros casos

401 en /auth/register o /auth/login. Revisa que el método sea POST. Un GET a esas rutas se trata como petición protegida y responde 401.

401 aunque ya tengas el token. Al copiar el token desde la respuesta JSON de Postman se pueden copiar las comillas dobles ("eyJhbGci..."). El token debe empezar directamente con eyJhbGci y terminar sin comillas. Revisa también que en Headers no haya un Authorization escrito a mano que pise al de la pestaña Authorization.

El token dejó de funcionar tras cambiar SECRET. Los tokens se firman con esa clave. Si la cambias, reinicia el gateway y el auth-service (docker compose up -d) y haz login de nuevo.

PowerShell dice que docker no se reconoce. Agrega su ruta a la sesión actual:

powershell
$env:Path += ";$env:LOCALAPPDATA\Programs\DockerDesktop\resources\bin"
docker version

Si en Server dice failed to connect, abre Docker Desktop desde el menú Inicio y espera a que diga Engine running. Para no repetir esa línea, cierra VS Code y todas las ventanas de PowerShell y abre una nueva.

Aviso en VS Code: "is a non-project file, only syntax errors are reported". No es un error del código. VS Code no detecta los proyectos Maven porque no hay un pom.xml en la raíz. Puedes ignorarlo (el build lo hace Docker) o ejecutar Java: Import Java Projects into Workspace desde Ctrl + Shift + P.

12. Ejecución sin Docker

Cada servicio también puede ejecutarse directamente desde IntelliJ o VS Code. En ese caso necesitas Java 21 y un MySQL propio (por ejemplo XAMPP) encendido en el puerto 3306. Por defecto los servicios se conectan a localhost:3306 con el usuario root sin contraseña, y la base de datos se crea sola. Si tu MySQL es distinto, define las variables de entorno DB_URL, DB_USER y DB_PASS.

Orden sugerido:

unidad-service
asignacion-service
evaluacion-service
auth-service
api-gateway

Para generar el jar de un servicio a mano:

bash
./mvnw clean package -DskipTests

Si ejecutas sin Docker, el gateway y el auth-service usan un SECRET por defecto de desarrollo. En Docker se usa siempre el de tu .env.

<details> <summary>Crear los proyectos desde cero (Spring Initializr)</summary>

Cada carpeta se puede crear en start.spring.io con: Maven, Java, Spring Boot 4.0.10, Jar, Properties, Java 21 y Group com.example. Después se copian encima el src/ y el Dockerfile de este proyecto.
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5

Dependencias del Initializr:

- **api-gateway:** Reactive Gateway, Spring Security, OAuth2 Resource Server.
- **auth-service:** Spring Web, Spring Data JPA, MySQL Driver, Lombok, Validation, Liquibase Migration.
- **unidad-service, asignacion-service y evaluacion-service:** Spring Web, Spring Data JPA, MySQL Driver, Liquibase Migration, Lombok, Validation, Spring Boot Actuator.

Solo en `auth-service`, agregar al `pom.xml` (dentro de `dependencies`):

xml
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
<<<<<<< HEAD
```

=======
>>>>>>> 353bedf1950e3b7598a6608556366281f8ad9cb5
</details>
