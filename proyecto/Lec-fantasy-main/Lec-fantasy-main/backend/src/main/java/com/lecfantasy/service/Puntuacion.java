package com.lecfantasy.service;

import com.lecfantasy.domain.EstadisticaJugadorPartida;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Sistema de puntos fantasy por partida. Todos los valores están aquí para poder
 * ajustarlos y explicarlos fácilmente en la memoria.
 *
 * Ejemplo: ADC 6/2/8 con 300 CS, 30 de visión y victoria
 *   6·3 + 8·2 − 2·1 + 300·0,02 + 30·0,05 + 2 = 41,5 puntos
 */
public final class Puntuacion {

    public static final BigDecimal KILL = new BigDecimal("3");
    public static final BigDecimal ASSIST = new BigDecimal("2");
    public static final BigDecimal DEATH = new BigDecimal("-1");
    public static final BigDecimal POR_CS = new BigDecimal("0.02");       // 1 punto cada 50 CS
    public static final BigDecimal POR_VISION = new BigDecimal("0.05");   // 1 punto cada 20 de visión
    public static final BigDecimal DOUBLE_KILL = new BigDecimal("1");
    public static final BigDecimal TRIPLE_KILL = new BigDecimal("2");
    public static final BigDecimal QUADRA_KILL = new BigDecimal("5");
    public static final BigDecimal PENTA_KILL = new BigDecimal("10");
    public static final BigDecimal FIRST_BLOOD = new BigDecimal("2");
    public static final BigDecimal VICTORIA = new BigDecimal("2");
    /** Se da al jugador con más puntos base de cada partida. */
    public static final BigDecimal MVP = new BigDecimal("3");

    private Puntuacion() {
    }

    /** Puntos sin el bonus de MVP. */
    public static BigDecimal base(EstadisticaJugadorPartida e) {
        BigDecimal p = KILL.multiply(BigDecimal.valueOf(e.getKills()))
                .add(ASSIST.multiply(BigDecimal.valueOf(e.getAssists())))
                .add(DEATH.multiply(BigDecimal.valueOf(e.getDeaths())))
                .add(POR_CS.multiply(BigDecimal.valueOf(e.getCs())))
                .add(POR_VISION.multiply(BigDecimal.valueOf(e.getVisionScore())))
                .add(DOUBLE_KILL.multiply(BigDecimal.valueOf(e.getDoubleKills())))
                .add(TRIPLE_KILL.multiply(BigDecimal.valueOf(e.getTripleKills())))
                .add(QUADRA_KILL.multiply(BigDecimal.valueOf(e.getQuadraKills())))
                .add(PENTA_KILL.multiply(BigDecimal.valueOf(e.getPentaKills())));
        if (e.isFirstBlood()) {
            p = p.add(FIRST_BLOOD);
        }
        if (e.isVictoria()) {
            p = p.add(VICTORIA);
        }
        return p.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal total(EstadisticaJugadorPartida e) {
        BigDecimal b = base(e);
        return e.isMvp() ? b.add(MVP) : b;
    }
}
