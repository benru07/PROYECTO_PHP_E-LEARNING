# LEC Fantasy

Proyecto de fin de grado: juego fantasy de la LEC (League of Legends EMEA Championship) con
**web (Vue)** y **app Android (Kotlin)** que comparten la misma **API REST (Spring Boot)** y la
misma base de datos **MySQL**.

```
lec-fantasy/
├── backend/          API REST · Spring Boot 3.5 · Java 21 · MySQL 8 · Flyway
├── web/              Web · Vue 3 + Vite + Vue Router + Pinia
├── android/          (siguiente fase) Kotlin
├── docs/             Modelo E-R actualizado (modelo-er.mmd / modelo-er.png)
└── docker-compose.yml  MySQL + backend en local
```

## Estado

| Parte | Estado |
|---|---|
| Base de datos (18 tablas, migraciones Flyway) | Hecho · tests en verde |
| API: registro, login, Google, recuperar contraseña | Hecho |
| API: ligas públicas/privadas, Liga Global, clasificaciones | Hecho |
| API: mercado, plantilla, alineación, mejoras | Hecho |
| API: jornadas, importación Leaguepedia / Oracle's Elixir, puntuación | Hecho |
| API: panel admin (usuarios, baneos, bloqueos, noticias, reportes, actividad) | Hecho |
| Web Vue (público, juego y panel de administración) | Hecho |
| App Android | Pendiente |

---

## 0. Flujo de trabajo con git

1. Actualizar localmente: `git switch develop` y luego `git pull origin develop`.
2. Crear la feature: `git switch -c feature/nombre-de-la-tarea`.
3. Trabajar y guardar: `git add .` y `git commit -m "Mensaje descriptivo"`.
4. Subir la rama: `git push -u origin feature/nombre-de-la-tarea`.
5. Abrir una Pull Request en GitHub desde la rama `feature/*` hacia `develop`.
6. Revisión: otro compañero revisa el código y da el visto bueno (*Approve*).
7. Fusión: se hace el *merge* de la PR y se borra la rama `feature` remota.
8. Para publicar una versión: Pull Request de `develop` hacia `main` (Railway despliega `main`).

---

## 1. Arrancar el backend en tu PC

> **Requisito: JDK 21 para ejecutar Gradle.** Gradle 8.14 (el máximo que admite Spring Boot 3.5)
> no funciona con Java 25 o superior. Instálalo con
> `winget install --id EclipseAdoptium.Temurin.21.JDK -e` y, antes de usar `gradlew`:
> ```powershell
> $env:JAVA_HOME = (Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -Filter "jdk-21*" | Select-Object -First 1).FullName
> ```
> o deja fijada la ruta en `C:\Users\<tu-usuario>\.gradle\gradle.properties` con
> `org.gradle.java.home=...`.

### Opción A: con MySQL instalado (XAMPP, MySQL Workbench...)

1. Crea la base de datos y el usuario:
   ```sql
   CREATE DATABASE lecfantasy CHARACTER SET utf8mb4;
   CREATE USER 'lec'@'localhost' IDENTIFIED BY 'lec';
   GRANT ALL ON lecfantasy.* TO 'lec'@'localhost';
   ```
2. En `backend/` (PowerShell):
   ```powershell
   $env:ADMIN_EMAIL="admin@lecfantasy.local"; $env:ADMIN_PASSWORD="admin12345"
   .\gradlew.bat bootRun
   ```
   Flyway crea las tablas al arrancar. La API queda en `http://localhost:8080` y la
   documentación interactiva en `http://localhost:8080/swagger-ui.html`.

### Opción B: con Docker

```powershell
docker compose up --build
```

### Tests

```powershell
cd backend
.\gradlew.bat test
```

Los tests usan H2 en memoria (modo MySQL) con las mismas migraciones, así que no necesitan
MySQL. `FlujoCompletoTest` recorre el juego entero: registro, liga privada, fichajes,
alineación, mejora, importación del CSV, cierre de jornada, baneo, bloqueo de jugador,
login con Google, reportes, noticias y borrado de usuario.

> Resultado actual: 13 tests, 0 fallos (`AutomatizacionTest` y `CalendarioTest` comprueban el modo automático y PandaScore).

---

## 1b. Arrancar la web en tu PC

Necesitas Node.js 20.19 o superior.

```powershell
cd web
npm install
npm run dev
```

Abre `http://localhost:5173`. En desarrollo, Vite redirige `/api` al backend de `localhost:8080`,
así que arranca antes el backend. Para el botón de Google, copia `.env.example` a `.env` y rellena
`VITE_GOOGLE_CLIENT_ID`.

**Pantallas**: inicio, objetivos, cómo funciona, noticias, login, registro, recuperar contraseña ·
juego (barra: Inicio · Mi equipo · Mercado · Mis ligas · Estadísticas · Mi cuenta): panel con cuenta atrás
del cierre de alineación, mi equipo sobre el mapa de la Grieta (alineación, mejoras, banquillo, puntos,
movimientos), mercado con ventana de fichaje, ficha del jugador con gráfica de precio y puntos por partida,
ligas y clasificación con racha y detalle del rival, explorador de estadísticas, mi cuenta (apariencia,
notificaciones, reglamento, datos y contraseña), jornadas y partida · administración: panel de control,
usuarios y baneos, bloqueo de jugadores, jugadores y precios, jornadas e importación, noticias, reportes,
actividad y configuración.

### Apariencia (selector de color)

En **Mi cuenta → Apariencia** cada usuario elige **modo oscuro o claro** y un **color de acento**
(8 predefinidos —naranja LEC, celeste, azul, cian, verde, violeta, rosa, oro— o uno libre con el
selector o escribiendo el hexadecimal). Se ve al momento y se guarda en su cuenta
(`PUT /api/usuarios/me/apariencia`), así que la app Android puede leerlo de `GET /api/usuarios/me`
(`tema`, `colorAcento`). Si un color no contrasta bien con el fondo (p. ej. oro en modo claro), la web
usa automáticamente una versión más oscura para los textos (`web/src/utils/tema.js`).

Las fuentes (Noto Sans y Noto Serif) van dentro de la web con `@fontsource`, sin depender de Google Fonts.
En el móvil la navegación pasa a una barra inferior.

---

## 2. Desplegar en Railway

1. Sube el repositorio a GitHub.
2. En Railway: **New Project → Deploy from GitHub repo** y elige el repo.
   En el servicio, **Settings → Root Directory = `backend`** (usa el `Dockerfile`).
3. Añade una base de datos: **New → Database → MySQL**.
4. En el servicio del backend, **Variables**:

| Variable | Valor |
|---|---|
| `MYSQLHOST` | `${{MySQL.MYSQLHOST}}` |
| `MYSQLPORT` | `${{MySQL.MYSQLPORT}}` |
| `MYSQLDATABASE` | `${{MySQL.MYSQLDATABASE}}` |
| `MYSQLUSER` | `${{MySQL.MYSQLUSER}}` |
| `MYSQLPASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `JWT_SECRET` | una cadena aleatoria de 32+ caracteres |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | el primer administrador |
| `CORS_ORIGINS` | URL de la web, p. ej. `https://lec-fantasy.up.railway.app` |
| `FRONTEND_URL` | la misma URL (para el enlace de recuperar contraseña) |
| `GOOGLE_CLIENT_IDS` | client IDs de Google (web y Android) separados por comas |
| `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | opcional, SMTP para correos |

5. **Settings → Networking → Generate Domain**. Comprueba `https://<dominio>/actuator/health`.
6. **Web**: en el mismo proyecto, **New → GitHub Repo** (el mismo repo) con **Root Directory = `web`**.
   Variables: `VITE_API_URL` = URL pública del backend y, si usas Google, `VITE_GOOGLE_CLIENT_ID`.
   Genera su dominio y ponlo en `CORS_ORIGINS` y `FRONTEND_URL` del backend.

Sin SMTP configurado, los correos (recuperar contraseña, aviso de baneo) se escriben en el log.

### Login con Google

En Google Cloud Console → *APIs & Services → Credentials*, crea un **OAuth client ID** de tipo
*Web application* (y otro *Android* cuando hagamos la app). Pon esos IDs en `GOOGLE_CLIENT_IDS`.
La web y la app obtienen un *ID token* de Google y lo envían a `POST /api/auth/google`.

---

## 3. Reglas del juego (implementadas)

- **Presupuesto**: cada liga define el suyo (Liga Global: 50 M). Todos los jugadores entran a 8 M.
- **Plantilla**: 7 jugadores, 5 titulares (uno por rol: TOP, JNG, MID, ADC, SUP) y 2 suplentes.
  Un fichaje va a su puesto si está libre; si no, al banquillo.
- **Mercado libre**: varios mánagers de la misma liga pueden tener al mismo jugador.
- **Mercado cerrado** mientras una jornada está `EN_CURSO` (ni fichajes, ni alineación, ni mejoras).
- **Solo puntúan los titulares.**
- **Mejoras**: se compran para un jugador de tu plantilla y se aplican a la próxima jornada
  programada. Si se cumple la condición y el jugador es titular, sus puntos se multiplican por
  `multiplicador_bono`; si no, se pierde lo pagado.
- **Precios**: al cerrar una jornada, cada jugador sube o baja hasta un 8 % según su media de
  puntos frente a una referencia de 25.
- **Liga Global**: todo usuario entra automáticamente al registrarse.

### Puntos por partida (`service/Puntuacion.java`)

| Acción | Puntos |
|---|---|
| Kill | +3 |
| Asistencia | +2 |
| Muerte | −1 |
| CS | +0,02 por CS (1 cada 50) |
| Visión | +0,05 por punto (1 cada 20) |
| Doble / triple / cuádruple / pentakill | +1 / +2 / +5 / +10 |
| First blood (kill o asistencia) | +2 |
| Victoria | +2 |
| MVP de la partida (más puntos base) | +3 |

Ejemplo: un ADC 6/2/8 con 300 CS, 30 de visión y victoria suma 41,5 puntos.

---

## 4. Datos reales de la LEC

El administrador gestiona cada jornada así:

1. **Crear jornada** (`PROGRAMADA`) con fechas y, opcionalmente, la página de Leaguepedia
   (p. ej. `LEC/2026 Season/Spring Season`).
2. **Iniciar** → `EN_CURSO` (cierra el mercado).
3. **Importar** las partidas jugadas en esas fechas:
   - **Leaguepedia** (botón, automático): kills, muertes, asistencias, CS, visión y victoria.
     No incluye multikills ni first blood.
   - **CSV de Oracle's Elixir** (subir archivo): estadísticas completas, multikills incluidas.
     Se descarga en oracleselixir.com → Tools → Downloads (archivo anual, todas las ligas;
     el importador se queda solo con la LEC).
   Los equipos y jugadores que no existan se crean solos. Reimportar no duplica partidas.
4. **Previsualizar** los puntos ("validar puntos").
5. **Finalizar** → puntúa a todos los equipos, resuelve mejoras, actualiza precios y reabre el mercado.

### Modo automático (por defecto, activado)

El backend lleva las jornadas solo, con datos de Leaguepedia (`service/AutomatizacionJornadas.java`):

- **Cada hora**: si hoy es la fecha de inicio de una jornada programada (y no hay otra en curso), la
  **inicia** → se cierran el mercado y las alineaciones.
- **Cada lunes a las 9:00** (hora de Madrid): para cada jornada en curso que ya ha terminado,
  **importa** sus partidas de Leaguepedia y la **finaliza** (puntos, mejoras y precios). Si Leaguepedia
  todavía no las tiene, la deja en curso y lo reintenta en la siguiente ejecución.
- Todo queda en el registro de actividad como «El sistema (automático)». En **Administración → Jornadas**
  se ve el estado, la última ejecución y un botón **Ejecutar ahora** (`POST /api/admin/automatizacion/ejecutar`).

- **Cada día a las 8:30**, si hay key de PandaScore (ver abajo): **crea las jornadas** de las próximas 3 semanas
  con partidos de la LEC (una por semana, del primer al último partido) y copia logos y siglas a los equipos.

Con PandaScore el administrador no tiene que hacer nada; sin él, solo **crear las jornadas con sus fechas** (y, si quiere,
la página de Leaguepedia). Se configura con variables de entorno:

| Variable | Por defecto | Qué hace |
|---|---|---|
| `AUTOMATIZACION_HABILITADA` | `true` | `false` para gestionar las jornadas a mano |
| `AUTOMATIZACION_CRON_IMPORTACION` | `0 0 9 * * MON` | cuándo importar y cerrar (segundo minuto hora día mes día-semana) |
| `AUTOMATIZACION_CRON_INICIO` | `0 5 * * * *` | cada cuánto comprobar si toca iniciar una jornada |
| `AUTOMATIZACION_FINALIZAR` | `true` | `false` para que solo importe y el admin revise los puntos antes de cerrar |

### Calendario con PandaScore (opcional)

[PandaScore](https://pandascore.co) da gratis (plan *Fixtures Only*) el calendario y los resultados de la LEC,
pero **no** las estadísticas de cada jugador, así que los puntos siguen saliendo de Leaguepedia / Oracle's Elixir.
Con la key, la web muestra en el panel los **próximos partidos** (con cuántos jugadores tuyos juegan) y las
jornadas se crean solas.

1. Copia tu token de https://app.pandascore.co/dashboard.
2. Ponlo **solo** como variable de entorno del backend: `PANDASCORE_TOKEN=...` (Railway → Variables;
   en local, `$env:PANDASCORE_TOKEN="..."` antes de `gradlew bootRun`). No lo escribas en el código, en
   `application.yml` ni en la web: el backend lo usa por ti y lo que llega al navegador es `GET /api/calendario`.
3. Opcional: `PANDASCORE_LIGA_ID` si la búsqueda automática de la liga «LEC» no la encuentra.

El panel guarda el calendario 15 minutos en memoria para no gastar peticiones (el plan gratuito permite 1.000/hora).

> Leaguepedia no incluye multikills ni first blood: con el modo automático las mejoras de Triple/Quadra/Penta
> Kill y First Blood no se cumplen nunca. Si las usas, sube el CSV de Oracle's Elixir antes del lunes
> (o pon `AUTOMATIZACION_FINALIZAR=false` y cierra a mano tras subirlo).

> Usa una sola fuente por jornada: Leaguepedia y Oracle's Elixir identifican las partidas de
> forma distinta y se contarían dos veces.

---

## 5. Resumen de la API

Todas las rutas empiezan por `/api`. Las marcadas con 🔒 necesitan `Authorization: Bearer <token>`;
las de `/api/admin` necesitan además rol ADMIN. Detalle completo en Swagger.

**Auth**: `POST /auth/registro`, `/auth/login`, `/auth/google`, `/auth/olvido-password`, `/auth/restablecer-password`

**Usuario 🔒**: `GET|PUT /usuarios/me`, `PUT /usuarios/me/apariencia` (`{ tema: OSCURO|CLARO, colorAcento: "#RRGGBB" }`), `PUT /usuarios/me/password`, `GET /usuarios/{id}`, `POST /reportes`

**Público**: `GET /calendario` (próximos partidos, PandaScore), `GET /equipos-pro`, `/jugadores?posicion=&equipoId=&q=`, `/jugadores/{id}`, `/jornadas`,
`/jornadas/{id}/partidas`, `/partidas/{id}`, `/mejoras`, `/noticias`, `/noticias/{id}`, `/ligas/publicas`

**Ligas 🔒**: `GET /ligas/mias`, `POST /ligas`, `POST /ligas/unirse` (código), `GET /ligas/{id}`,
`POST /ligas/{id}/unirse`, `GET /ligas/{id}/clasificacion?jornadaId=`, `DELETE /ligas/{id}/abandonar`, `DELETE /ligas/{id}`

**Equipo 🔒**: `GET /equipos/{id}`, `POST /equipos/{id}/fichajes`, `DELETE /equipos/{id}/plantilla/{jugadorId}`,
`PUT /equipos/{id}/alineacion`, `GET /equipos/{id}/transacciones`, `GET /equipos/{id}/puntuaciones`,
`GET|POST /equipos/{id}/mejoras`, `DELETE /equipos/{id}/mejoras/{mejoraAplicadaId}`

**Admin 🔒**: `GET /admin/dashboard`, `/admin/actividad`, `/admin/usuarios?q=&estado=&pais=`,
`GET /admin/usuarios/{id}`, `POST|DELETE /admin/usuarios/{id}/baneo`, `PUT /admin/usuarios/{id}/rol`,
`DELETE /admin/usuarios/{id}`, `GET|POST /admin/jugadores`, `PUT /admin/jugadores/{id}`,
`POST|DELETE /admin/jugadores/{id}/bloqueo`, `POST|PUT /admin/equipos-pro`, `POST|PUT|DELETE /admin/jornadas`,
`POST /admin/jornadas/{id}/iniciar`, `/importar/leaguepedia`, `/importar/csv`, `GET /previsualizacion`,
`POST /finalizar`, `GET /admin/automatizacion`, `POST /admin/automatizacion/ejecutar`, `DELETE /admin/partidas/{id}`, `GET|POST|PUT /admin/mejoras`,
`GET|POST|PUT|DELETE /admin/noticias`, `GET /admin/reportes?estado=`, `POST /admin/reportes/{id}/resolver`

---

## 6. Cambios respecto al E-R original

Ver `docs/modelo-er.png` (los campos nuevos están marcados como NUEVO). Resumen:

- **Tablas nuevas**: `equipo_pro`, `puntuacion_jornada`, `noticia`, `reporte`, `baneo`,
  `registro_actividad`, `password_reset_token`.
- **usuario**: fecha de nacimiento, país, equipo favorito, Riot ID, noticias, Google, fechas de registro y acceso.
- **jugador_real / partida_real**: los equipos pasan de texto a clave foránea a `equipo_pro`;
  id externo para importar; bloqueo de jugadores.
- **mejora**: `condicion` + `umbral` para que el sistema pueda evaluar si se cumple.
- **estadistica_jugador_partida**: campeón, dobles, victoria y equipo con el que jugó.
- **V3 (apariencia y precios)**: `usuario.tema` + `usuario.color_acento`; `jugador_real.valor_anterior`
  (variación ▲/▼ del mercado) y la tabla nueva `historial_valor` (un registro por cambio de precio al
  cerrar jornada o al editarlo el admin), que alimenta la gráfica de evolución del precio.
  La clasificación (`GET /ligas/{id}/clasificacion`) devuelve además `puntosUltimaJornada`,
  `valorPlantilla` y `racha` (últimas 5 jornadas, por encima o por debajo de la media de la liga).
- Tipos `int` → `BIGINT` en las claves y `puntos` en decimal (los puntos llevan decimales por el CS y la visión).
