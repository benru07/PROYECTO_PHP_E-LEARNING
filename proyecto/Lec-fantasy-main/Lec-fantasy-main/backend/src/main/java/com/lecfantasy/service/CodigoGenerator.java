package com.lecfantasy.service;

import java.security.SecureRandom;

public final class CodigoGenerator {

    // Sin 0/O/1/I para que no se confundan al dictarlo
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodigoGenerator() {
    }

    public static String codigo(int longitud) {
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            sb.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }
}
