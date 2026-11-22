package com.lecfantasy.service.calendario;

import java.time.Instant;

/**
 * Un partido de la LEC en el calendario de PandaScore (puede ser un Bo1, Bo3...).
 *
 * @param estado not_started, running, finished, postponed o canceled
 */
public record PartidoCalendario(long id, Instant fecha, String estado, Equipo equipoA, Equipo equipoB,
                                Integer marcadorA, Integer marcadorB, Long ganadorId, int mejorDe,
                                String competicion) {

    /** Equipo según PandaScore. id es el de PandaScore, no el de nuestra base de datos. */
    public record Equipo(long id, String nombre, String siglas, String logoUrl) {
    }

    public boolean cancelado() {
        return "canceled".equals(estado);
    }
}
