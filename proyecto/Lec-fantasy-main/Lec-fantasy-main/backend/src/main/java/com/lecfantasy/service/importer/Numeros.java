package com.lecfantasy.service.importer;

final class Numeros {

    private Numeros() {
    }

    /** Convierte "12", "12.0", "" o null en entero (0 si está vacío o no es número). */
    static int entero(String s) {
        if (s == null) {
            return 0;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(t));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static Integer enteroONull(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return entero(s);
    }

    static boolean verdadero(String s) {
        if (s == null) {
            return false;
        }
        String t = s.trim().toLowerCase();
        return t.equals("1") || t.equals("1.0") || t.equals("true") || t.equals("yes") || t.equals("win");
    }
}
