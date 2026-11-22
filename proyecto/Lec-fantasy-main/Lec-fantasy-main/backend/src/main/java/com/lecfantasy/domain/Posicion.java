package com.lecfantasy.domain;

import java.util.Locale;

public enum Posicion {
    TOP, JNG, MID, ADC, SUP;

    /** Convierte los nombres de rol de Leaguepedia / Oracle's Elixir a nuestra posición. */
    public static Posicion desdeTextoExterno(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim().toLowerCase(Locale.ROOT);
        switch (t) {
            case "top":
                return TOP;
            case "jng": case "jungle": case "jungla": case "jg":
                return JNG;
            case "mid": case "middle":
                return MID;
            case "bot": case "adc": case "bottom": case "ad carry":
                return ADC;
            case "sup": case "support": case "supp":
                return SUP;
            default:
                return null;
        }
    }
}
