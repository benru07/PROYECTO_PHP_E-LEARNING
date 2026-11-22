package com.lecfantasy.service.importer;

import com.lecfantasy.domain.Posicion;

import java.time.LocalDateTime;

/** Una fila "jugador en una partida" leída de cualquier fuente externa. */
public record StatLine(
        String partidaId,
        LocalDateTime fechaHora,
        String equipo,
        Boolean ladoAzul,
        String jugador,
        String jugadorIdExterno,
        Posicion posicion,
        String campeon,
        int kills,
        int deaths,
        int assists,
        int cs,
        int visionScore,
        int doubleKills,
        int tripleKills,
        int quadraKills,
        int pentaKills,
        boolean firstBlood,
        boolean victoria,
        Integer duracionSegundos) {
}
