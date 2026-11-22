package com.lecfantasy.service;

import com.lecfantasy.domain.EstadisticaJugadorPartida;
import com.lecfantasy.domain.Mejora;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Decide si la condición de una mejora se cumple con las partidas del jugador en la jornada. */
public final class EvaluadorMejoras {

    private static final BigDecimal KDA_POR_DEFECTO = new BigDecimal("3");

    private EvaluadorMejoras() {
    }

    public static boolean cumple(Mejora mejora, List<EstadisticaJugadorPartida> partidas) {
        if (partidas.isEmpty()) {
            return false; // si no juega, la mejora falla
        }
        int kills = 0, deaths = 0, assists = 0, triples = 0, quadras = 0, pentas = 0;
        boolean firstBlood = false, mvp = false, todasGanadas = true;
        for (EstadisticaJugadorPartida e : partidas) {
            kills += e.getKills();
            deaths += e.getDeaths();
            assists += e.getAssists();
            triples += e.getTripleKills();
            quadras += e.getQuadraKills();
            pentas += e.getPentaKills();
            firstBlood |= e.isFirstBlood();
            mvp |= e.isMvp();
            todasGanadas &= e.isVictoria();
        }
        switch (mejora.getCondicion()) {
            case TRIPLE_KILL:
                return triples + quadras + pentas > 0;
            case QUADRA_KILL:
                return quadras + pentas > 0;
            case PENTA_KILL:
                return pentas > 0;
            case FIRST_BLOOD:
                return firstBlood;
            case MVP:
                return mvp;
            case CERO_MUERTES:
                return deaths == 0;
            case KDA_MINIMO:
                BigDecimal umbral = mejora.getUmbral() == null ? KDA_POR_DEFECTO : mejora.getUmbral();
                BigDecimal kda = BigDecimal.valueOf(kills + assists)
                        .divide(BigDecimal.valueOf(Math.max(1, deaths)), 2, RoundingMode.HALF_UP);
                return kda.compareTo(umbral) >= 0;
            case VICTORIAS:
                return todasGanadas;
            default:
                return false;
        }
    }
}
