package com.lecfantasy.service.importer;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.Posicion;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Lee el CSV anual de partidas de Oracle's Elixir (oracleselixir.com/tools/downloads).
 * Hay 12 filas por partida: 10 jugadores y 2 de equipo (position = "team"), que se descartan.
 * Las cabeceras se buscan sin distinguir mayúsculas, espacios ni guiones bajos.
 */
@Component
public class OraclesElixirParser {

    private static final List<DateTimeFormatter> FORMATOS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm"),
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    public record Resultado(List<StatLine> lineas, int ignoradas) {
    }

    public Resultado leer(InputStream entrada, String liga) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String cabecera = r.readLine();
            if (cabecera == null) {
                throw ApiException.peticionInvalida("El CSV está vacío");
            }
            if (cabecera.startsWith("﻿")) {
                cabecera = cabecera.substring(1);
            }
            Map<String, Integer> col = new HashMap<>();
            List<String> nombres = partir(cabecera);
            for (int i = 0; i < nombres.size(); i++) {
                col.putIfAbsent(normalizar(nombres.get(i)), i);
            }
            for (String obligatoria : List.of("gameid", "league", "date", "position", "playername", "teamname")) {
                if (!col.containsKey(obligatoria)) {
                    throw ApiException.peticionInvalida("El CSV no parece de Oracle's Elixir: falta la columna «" + obligatoria + "»");
                }
            }
            List<StatLine> lineas = new ArrayList<>();
            int ignoradas = 0;
            String linea;
            while ((linea = r.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                List<String> v = partir(linea);
                if (!liga.equalsIgnoreCase(valor(v, col, "league"))) {
                    continue;
                }
                String posicionTexto = valor(v, col, "position");
                if ("team".equalsIgnoreCase(posicionTexto)) {
                    continue;
                }
                Posicion posicion = Posicion.desdeTextoExterno(posicionTexto);
                LocalDateTime fecha = fecha(valor(v, col, "date"));
                String jugador = valor(v, col, "playername");
                if (posicion == null || fecha == null || jugador == null || jugador.isBlank()) {
                    ignoradas++;
                    continue;
                }
                int cs = col.containsKey("totalcs") ? Numeros.entero(valor(v, col, "totalcs"))
                        : Numeros.entero(valor(v, col, "minionkills")) + Numeros.entero(valor(v, col, "monsterkills"));
                boolean firstBlood = col.containsKey("firstbloodkill")
                        ? Numeros.verdadero(valor(v, col, "firstbloodkill")) || Numeros.verdadero(valor(v, col, "firstbloodassist"))
                        : Numeros.verdadero(valor(v, col, "firstblood"));
                String lado = valor(v, col, "side");
                String playerId = valor(v, col, "playerid");
                lineas.add(new StatLine(
                        "OE:" + valor(v, col, "gameid"),
                        fecha,
                        valor(v, col, "teamname"),
                        lado == null || lado.isBlank() ? null : lado.trim().equalsIgnoreCase("blue"),
                        jugador.trim(),
                        playerId == null || playerId.isBlank() ? null : "OE:" + playerId.trim(),
                        posicion,
                        valor(v, col, "champion"),
                        Numeros.entero(valor(v, col, "kills")),
                        Numeros.entero(valor(v, col, "deaths")),
                        Numeros.entero(valor(v, col, "assists")),
                        cs,
                        Numeros.entero(valor(v, col, "visionscore")),
                        Numeros.entero(valor(v, col, "doublekills")),
                        Numeros.entero(valor(v, col, "triplekills")),
                        Numeros.entero(valor(v, col, "quadrakills")),
                        Numeros.entero(valor(v, col, "pentakills")),
                        firstBlood,
                        Numeros.verdadero(valor(v, col, "result")),
                        Numeros.enteroONull(valor(v, col, "gamelength"))));
            }
            return new Resultado(lineas, ignoradas);
        } catch (IOException e) {
            throw ApiException.peticionInvalida("No se pudo leer el CSV: " + e.getMessage());
        }
    }

    static String normalizar(String cabecera) {
        return cabecera.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static String valor(List<String> v, Map<String, Integer> col, String nombre) {
        Integer i = col.get(nombre);
        return i == null || i >= v.size() ? null : v.get(i);
    }

    private static LocalDateTime fecha(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        String t = s.trim();
        for (DateTimeFormatter f : FORMATOS) {
            try {
                return LocalDateTime.parse(t, f);
            } catch (DateTimeParseException ignored) {
                // probamos el siguiente formato
            }
        }
        try {
            return LocalDate.parse(t.length() >= 10 ? t.substring(0, 10) : t).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Separa una línea CSV respetando comillas dobles. */
    static List<String> partir(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (entreComillas) {
                if (c == '"') {
                    if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                        actual.append('"');
                        i++;
                    } else {
                        entreComillas = false;
                    }
                } else {
                    actual.append(c);
                }
            } else if (c == '"') {
                entreComillas = true;
            } else if (c == ',') {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString());
        return campos;
    }
}
