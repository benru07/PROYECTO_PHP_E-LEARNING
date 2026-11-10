package com.lecfantasy.service;

import com.lecfantasy.domain.TipoLiga;

/** Constantes de las reglas del juego, en un único sitio para poder ajustarlas. */
public final class ReglasJuego {

    private ReglasJuego() {
    }

    /** Plantilla: 5 titulares + 2 suplentes. */
    public static final int MAX_JUGADORES_PLANTILLA = 7;

    public static final String NOMBRE_LIGA_GLOBAL = "Liga Global";
    public static final TipoLiga TIPO_LIGA_GLOBAL = TipoLiga.PUBLICO;
    public static final int MAX_PARTICIPANTES_GLOBAL = 1_000_000;

    /** Duración del enlace de "¿Olvidaste tu contraseña?". */
    public static final int HORAS_VALIDEZ_RESET = 1;
}
