package com.lecfantasy.domain;

/** Hueco de la plantilla: 5 titulares (uno por rol) y 2 suplentes. */
public enum Slot {
    TOP, JNG, MID, ADC, SUP, BANQUILLO_1, BANQUILLO_2;

    public boolean esTitular() {
        return this != BANQUILLO_1 && this != BANQUILLO_2;
    }

    public static Slot titularDe(Posicion posicion) {
        return Slot.valueOf(posicion.name());
    }

    /** Un slot de titular solo admite jugadores de ese rol; el banquillo admite cualquiera. */
    public boolean admite(Posicion posicion) {
        return !esTitular() || this.name().equals(posicion.name());
    }
}
