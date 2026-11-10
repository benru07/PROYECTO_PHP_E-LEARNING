-- =====================================================================
-- V3 · Apariencia del usuario e historial de valor de los jugadores
-- SQL compatible con MySQL 8 y con H2 en modo MySQL (tests).
-- =====================================================================

-- ---------- USUARIO: tema elegido en "Mi cuenta" ----------
-- Lo comparten la web y la app: modo OSCURO/CLARO y color de acento en hexadecimal.
ALTER TABLE usuario ADD COLUMN tema VARCHAR(10) NOT NULL DEFAULT 'OSCURO';
ALTER TABLE usuario ADD COLUMN color_acento VARCHAR(7) NOT NULL DEFAULT '#FF6B1A';

-- ---------- JUGADOR_REAL: valor antes del último cambio ----------
-- Permite mostrar la variación (▲/▼ %) en el mercado sin consultar el historial.
ALTER TABLE jugador_real ADD COLUMN valor_anterior DECIMAL(14,2);

-- ---------- HISTORIAL_VALOR (NUEVA) ----------
-- Una fila por jugador cada vez que cambia su precio: al cerrar una jornada
-- (id_jornada informado) o cuando un administrador lo edita (id_jornada NULL).
CREATE TABLE historial_valor (
    id_historial     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_jugador_real  BIGINT         NOT NULL,
    id_jornada       BIGINT,
    valor            DECIMAL(14,2)  NOT NULL,
    fecha            DATETIME(6)    NOT NULL,
    CONSTRAINT fk_historial_jugador FOREIGN KEY (id_jugador_real)
        REFERENCES jugador_real (id_jugador_real) ON DELETE CASCADE,
    CONSTRAINT fk_historial_jornada FOREIGN KEY (id_jornada)
        REFERENCES jornada (id_jornada) ON DELETE SET NULL
);

CREATE INDEX idx_historial_jugador ON historial_valor (id_jugador_real, fecha);
