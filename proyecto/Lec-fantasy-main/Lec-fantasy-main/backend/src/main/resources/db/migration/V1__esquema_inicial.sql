-- =====================================================================
-- LEC Fantasy - Esquema inicial
-- Basado en el diagrama E-R original, ampliado con lo que piden las
-- pantallas (registro, administración, noticias, reportes, baneos...).
-- SQL compatible con MySQL 8 y con H2 en modo MySQL (tests).
-- =====================================================================

-- ---------- Equipos profesionales de la LEC (NUEVA) ----------
CREATE TABLE equipo_pro (
    id_equipo_pro   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    siglas          VARCHAR(10),
    logo_url        VARCHAR(500),
    CONSTRAINT uk_equipo_pro_nombre UNIQUE (nombre)
);

-- ---------- USUARIO (ampliada) ----------
CREATE TABLE usuario (
    id_usuario          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(30)  NOT NULL,
    email               VARCHAR(255) NOT NULL,
    password_hash       VARCHAR(100),
    rol                 VARCHAR(10)  NOT NULL,
    fecha_nacimiento    DATE,
    pais                VARCHAR(2),
    id_equipo_favorito  BIGINT,
    riot_id             VARCHAR(40),
    acepta_noticias     BOOLEAN      NOT NULL DEFAULT FALSE,
    google_id           VARCHAR(64),
    fecha_registro      DATETIME(6)  NOT NULL,
    ultimo_acceso       DATETIME(6),
    CONSTRAINT uk_usuario_nombre UNIQUE (nombre),
    CONSTRAINT uk_usuario_email  UNIQUE (email),
    CONSTRAINT uk_usuario_google UNIQUE (google_id),
    CONSTRAINT fk_usuario_equipo_fav FOREIGN KEY (id_equipo_favorito)
        REFERENCES equipo_pro (id_equipo_pro) ON DELETE SET NULL
);

-- ---------- GRUPO_LIGA (ampliada) ----------
CREATE TABLE grupo_liga (
    id_grupo             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre               VARCHAR(60)    NOT NULL,
    tipo                 VARCHAR(10)    NOT NULL,
    codigo_invitacion    VARCHAR(12)    NOT NULL,
    presupuesto_inicial  DECIMAL(14,2)  NOT NULL,
    max_participantes    INT            NOT NULL,
    es_global            BOOLEAN        NOT NULL DEFAULT FALSE,
    id_creador           BIGINT,
    fecha_creacion       DATETIME(6)    NOT NULL,
    CONSTRAINT uk_grupo_codigo UNIQUE (codigo_invitacion),
    CONSTRAINT fk_grupo_creador FOREIGN KEY (id_creador)
        REFERENCES usuario (id_usuario) ON DELETE SET NULL
);

-- ---------- EQUIPO_FANTASY (ampliada) ----------
CREATE TABLE equipo_fantasy (
    id_equipo_fantasy  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario         BIGINT         NOT NULL,
    id_grupo           BIGINT         NOT NULL,
    nombre             VARCHAR(40)    NOT NULL,
    dinero_disponible  DECIMAL(14,2)  NOT NULL,
    puntos_totales     DECIMAL(10,2)  NOT NULL DEFAULT 0,
    fecha_union        DATETIME(6)    NOT NULL,
    version            BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uk_equipo_usuario_grupo UNIQUE (id_usuario, id_grupo),
    CONSTRAINT fk_equipo_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_equipo_grupo FOREIGN KEY (id_grupo)
        REFERENCES grupo_liga (id_grupo) ON DELETE CASCADE
);

-- ---------- JUGADOR_REAL (ampliada: equipo_pro pasa a FK) ----------
CREATE TABLE jugador_real (
    id_jugador_real  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname         VARCHAR(40)    NOT NULL,
    nombre_real      VARCHAR(100),
    id_equipo_pro    BIGINT,
    posicion         VARCHAR(3)     NOT NULL,
    valor_mercado    DECIMAL(14,2)  NOT NULL,
    foto_url         VARCHAR(500),
    id_externo       VARCHAR(100),
    bloqueado        BOOLEAN        NOT NULL DEFAULT FALSE,
    motivo_bloqueo   VARCHAR(255),
    CONSTRAINT uk_jugador_nickname UNIQUE (nickname),
    CONSTRAINT uk_jugador_externo  UNIQUE (id_externo),
    CONSTRAINT fk_jugador_equipo_pro FOREIGN KEY (id_equipo_pro)
        REFERENCES equipo_pro (id_equipo_pro) ON DELETE SET NULL
);

-- ---------- PLANTILLA_JUGADOR (ampliada) ----------
CREATE TABLE plantilla_jugador (
    id_plantilla       BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_equipo_fantasy  BIGINT         NOT NULL,
    id_jugador_real    BIGINT         NOT NULL,
    estado             VARCHAR(10)    NOT NULL,
    posicion_slot      VARCHAR(12)    NOT NULL,
    precio_compra      DECIMAL(14,2)  NOT NULL,
    fecha_fichaje      DATETIME(6)    NOT NULL,
    CONSTRAINT uk_plantilla_equipo_jugador UNIQUE (id_equipo_fantasy, id_jugador_real),
    CONSTRAINT fk_plantilla_equipo FOREIGN KEY (id_equipo_fantasy)
        REFERENCES equipo_fantasy (id_equipo_fantasy) ON DELETE CASCADE,
    CONSTRAINT fk_plantilla_jugador FOREIGN KEY (id_jugador_real)
        REFERENCES jugador_real (id_jugador_real) ON DELETE CASCADE
);

-- ---------- TRANSACCION ----------
CREATE TABLE transaccion (
    id_transaccion     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_equipo_fantasy  BIGINT         NOT NULL,
    id_jugador_real    BIGINT         NOT NULL,
    tipo               VARCHAR(10)    NOT NULL,
    precio             DECIMAL(14,2)  NOT NULL,
    fecha              DATETIME(6)    NOT NULL,
    CONSTRAINT fk_transaccion_equipo FOREIGN KEY (id_equipo_fantasy)
        REFERENCES equipo_fantasy (id_equipo_fantasy) ON DELETE CASCADE,
    CONSTRAINT fk_transaccion_jugador FOREIGN KEY (id_jugador_real)
        REFERENCES jugador_real (id_jugador_real) ON DELETE CASCADE
);

-- ---------- JORNADA (ampliada) ----------
CREATE TABLE jornada (
    id_jornada          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100)  NOT NULL,
    fecha_inicio        DATE          NOT NULL,
    fecha_fin           DATE          NOT NULL,
    estado              VARCHAR(12)   NOT NULL,
    fuente_overview     VARCHAR(200),
    fecha_finalizacion  DATETIME(6)
);

-- ---------- PARTIDA_REAL (ampliada: equipos como FK) ----------
CREATE TABLE partida_real (
    id_partida          BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_jornada          BIGINT        NOT NULL,
    id_externo          VARCHAR(150)  NOT NULL,
    id_equipo_azul      BIGINT,
    id_equipo_rojo      BIGINT,
    id_ganador          BIGINT,
    duracion_segundos   INT,
    fecha_hora          DATETIME(6)   NOT NULL,
    CONSTRAINT uk_partida_externo UNIQUE (id_externo),
    CONSTRAINT fk_partida_jornada FOREIGN KEY (id_jornada)
        REFERENCES jornada (id_jornada) ON DELETE CASCADE,
    CONSTRAINT fk_partida_azul FOREIGN KEY (id_equipo_azul)
        REFERENCES equipo_pro (id_equipo_pro) ON DELETE SET NULL,
    CONSTRAINT fk_partida_rojo FOREIGN KEY (id_equipo_rojo)
        REFERENCES equipo_pro (id_equipo_pro) ON DELETE SET NULL,
    CONSTRAINT fk_partida_ganador FOREIGN KEY (id_ganador)
        REFERENCES equipo_pro (id_equipo_pro) ON DELETE SET NULL
);

-- ---------- ESTADISTICA_JUGADOR_PARTIDA (ampliada) ----------
CREATE TABLE estadistica_jugador_partida (
    id_estadistica   BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_partida       BIGINT        NOT NULL,
    id_jugador_real  BIGINT        NOT NULL,
    id_equipo_pro    BIGINT,
    campeon          VARCHAR(40),
    kills            INT           NOT NULL DEFAULT 0,
    deaths           INT           NOT NULL DEFAULT 0,
    assists          INT           NOT NULL DEFAULT 0,
    cs               INT           NOT NULL DEFAULT 0,
    vision_score     INT           NOT NULL DEFAULT 0,
    double_kills     INT           NOT NULL DEFAULT 0,
    triple_kills     INT           NOT NULL DEFAULT 0,
    quadra_kills     INT           NOT NULL DEFAULT 0,
    penta_kills      INT           NOT NULL DEFAULT 0,
    first_blood      BOOLEAN       NOT NULL DEFAULT FALSE,
    victoria         BOOLEAN       NOT NULL DEFAULT FALSE,
    mvp              BOOLEAN       NOT NULL DEFAULT FALSE,
    puntos_fantasy   DECIMAL(8,2)  NOT NULL DEFAULT 0,
    CONSTRAINT uk_estadistica_partida_jugador UNIQUE (id_partida, id_jugador_real),
    CONSTRAINT fk_estadistica_partida FOREIGN KEY (id_partida)
        REFERENCES partida_real (id_partida) ON DELETE CASCADE,
    CONSTRAINT fk_estadistica_jugador FOREIGN KEY (id_jugador_real)
        REFERENCES jugador_real (id_jugador_real) ON DELETE CASCADE,
    CONSTRAINT fk_estadistica_equipo FOREIGN KEY (id_equipo_pro)
        REFERENCES equipo_pro (id_equipo_pro) ON DELETE SET NULL
);

-- ---------- MEJORA (ampliada: condición evaluable) ----------
CREATE TABLE mejora (
    id_mejora          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(60)    NOT NULL,
    descripcion        VARCHAR(255),
    precio             DECIMAL(14,2)  NOT NULL,
    multiplicador_bono INT            NOT NULL,
    condicion          VARCHAR(20)    NOT NULL,
    umbral             DECIMAL(6,2),
    activa             BOOLEAN        NOT NULL DEFAULT TRUE
);

-- ---------- MEJORA_APLICADA (ampliada) ----------
CREATE TABLE mejora_aplicada (
    id_mejora_aplicada  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_equipo_fantasy   BIGINT         NOT NULL,
    id_jugador_real     BIGINT         NOT NULL,
    id_mejora           BIGINT         NOT NULL,
    id_jornada          BIGINT         NOT NULL,
    estado              VARCHAR(10)    NOT NULL,
    precio_pagado       DECIMAL(14,2)  NOT NULL,
    puntos_extra        DECIMAL(8,2)   NOT NULL DEFAULT 0,
    CONSTRAINT uk_mejora_equipo_jugador_jornada UNIQUE (id_equipo_fantasy, id_jugador_real, id_jornada),
    CONSTRAINT fk_mejora_ap_equipo FOREIGN KEY (id_equipo_fantasy)
        REFERENCES equipo_fantasy (id_equipo_fantasy) ON DELETE CASCADE,
    CONSTRAINT fk_mejora_ap_jugador FOREIGN KEY (id_jugador_real)
        REFERENCES jugador_real (id_jugador_real) ON DELETE CASCADE,
    CONSTRAINT fk_mejora_ap_mejora FOREIGN KEY (id_mejora)
        REFERENCES mejora (id_mejora) ON DELETE CASCADE,
    CONSTRAINT fk_mejora_ap_jornada FOREIGN KEY (id_jornada)
        REFERENCES jornada (id_jornada) ON DELETE CASCADE
);

-- ---------- PUNTUACION_JORNADA (NUEVA: historial por jornada) ----------
CREATE TABLE puntuacion_jornada (
    id_puntuacion      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_equipo_fantasy  BIGINT        NOT NULL,
    id_jornada         BIGINT        NOT NULL,
    puntos_base        DECIMAL(10,2) NOT NULL,
    puntos_mejoras     DECIMAL(10,2) NOT NULL,
    puntos_totales     DECIMAL(10,2) NOT NULL,
    CONSTRAINT uk_puntuacion_equipo_jornada UNIQUE (id_equipo_fantasy, id_jornada),
    CONSTRAINT fk_puntuacion_equipo FOREIGN KEY (id_equipo_fantasy)
        REFERENCES equipo_fantasy (id_equipo_fantasy) ON DELETE CASCADE,
    CONSTRAINT fk_puntuacion_jornada FOREIGN KEY (id_jornada)
        REFERENCES jornada (id_jornada) ON DELETE CASCADE
);

-- ---------- NOTICIA (NUEVA) ----------
CREATE TABLE noticia (
    id_noticia         BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo             VARCHAR(200)   NOT NULL,
    resumen            VARCHAR(500),
    contenido          VARCHAR(10000) NOT NULL,
    imagen_url         VARCHAR(500),
    id_autor           BIGINT,
    publicada          BOOLEAN        NOT NULL DEFAULT TRUE,
    fecha_publicacion  DATETIME(6)    NOT NULL,
    CONSTRAINT fk_noticia_autor FOREIGN KEY (id_autor)
        REFERENCES usuario (id_usuario) ON DELETE SET NULL
);

-- ---------- REPORTE (NUEVA) ----------
CREATE TABLE reporte (
    id_reporte        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reportador     BIGINT        NOT NULL,
    id_reportado      BIGINT        NOT NULL,
    motivo            VARCHAR(30)   NOT NULL,
    descripcion       VARCHAR(1000),
    estado            VARCHAR(12)   NOT NULL,
    fecha             DATETIME(6)   NOT NULL,
    id_admin          BIGINT,
    resolucion        VARCHAR(500),
    fecha_resolucion  DATETIME(6),
    CONSTRAINT fk_reporte_reportador FOREIGN KEY (id_reportador)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_reporte_reportado FOREIGN KEY (id_reportado)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_reporte_admin FOREIGN KEY (id_admin)
        REFERENCES usuario (id_usuario) ON DELETE SET NULL
);

-- ---------- BANEO (NUEVA: historial de baneos) ----------
CREATE TABLE baneo (
    id_baneo     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   BIGINT        NOT NULL,
    id_admin     BIGINT,
    inicio       DATETIME(6)   NOT NULL,
    fin          DATETIME(6),
    motivo       VARCHAR(30)   NOT NULL,
    comentario   VARCHAR(500),
    notificado   BOOLEAN       NOT NULL DEFAULT FALSE,
    revocado_en  DATETIME(6),
    CONSTRAINT fk_baneo_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_baneo_admin FOREIGN KEY (id_admin)
        REFERENCES usuario (id_usuario) ON DELETE SET NULL
);

-- ---------- REGISTRO_ACTIVIDAD (NUEVA) ----------
CREATE TABLE registro_actividad (
    id_actividad  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_admin      BIGINT,
    admin_nombre  VARCHAR(30),
    tipo          VARCHAR(30)   NOT NULL,
    descripcion   VARCHAR(500)  NOT NULL,
    fecha         DATETIME(6)   NOT NULL,
    CONSTRAINT fk_actividad_admin FOREIGN KEY (id_admin)
        REFERENCES usuario (id_usuario) ON DELETE SET NULL
);
CREATE INDEX idx_actividad_fecha ON registro_actividad (fecha);

-- ---------- PASSWORD_RESET_TOKEN (NUEVA) ----------
CREATE TABLE password_reset_token (
    id_token     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   BIGINT       NOT NULL,
    token_hash   VARCHAR(64)  NOT NULL,
    expira       DATETIME(6)  NOT NULL,
    usado        BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_reset_token UNIQUE (token_hash),
    CONSTRAINT fk_reset_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE
);
