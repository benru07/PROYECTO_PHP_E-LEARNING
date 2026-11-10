package com.lecfantasy.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lecfantasy.security.GoogleTokenVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Recorre el juego completo contra una base de datos H2 en memoria (modo MySQL) con las
 * mismas migraciones Flyway que producción: registro, ligas, mercado, mejoras, importación
 * de estadísticas, cierre de jornada, moderación y administración.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlujoCompletoTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private GoogleTokenVerifier google;

    @Test
    void flujoCompletoDelJuego() throws Exception {
        // ---------- Registro y login ----------
        String ana = token(registrar("ana_test", "ana@test.com"));
        String bea = token(registrar("bea_test", "bea@test.com"));

        enviar(post("/api/auth/registro"), null, registro("ana_test", "otra@test.com"), 409);
        Map<String, Object> distintas = registro("cris_test", "cris@test.com");
        distintas.put("repetirPassword", "otraCosa123");
        enviar(post("/api/auth/registro"), null, distintas, 400);
        JsonNode invalido = enviar(post("/api/auth/registro"), null, Map.of("nombre", "x"), 400);
        assertThat(invalido.get("campos").has("email")).isTrue();

        enviar(post("/api/auth/login"), null, Map.of("identificador", "ana_test", "password", "malamala1"), 401);
        String admin = token(enviar(post("/api/auth/login"), null,
                Map.of("identificador", "admin@test.com", "password", "admin12345"), 200));

        JsonNode yoAna = enviar(get("/api/usuarios/me"), ana, null, 200);
        assertThat(yoAna.get("nombre").asText()).isEqualTo("ana_test");

        // ---------- Apariencia (selector de color de "Mi cuenta") ----------
        assertThat(yoAna.get("tema").asText()).isEqualTo("OSCURO");
        assertThat(yoAna.get("colorAcento").asText()).isEqualTo("#FF6B1A");
        JsonNode apariencia = enviar(put("/api/usuarios/me/apariencia"), ana,
                Map.of("tema", "CLARO", "colorAcento", "#1fb6ff"), 200);
        assertThat(apariencia.get("tema").asText()).isEqualTo("CLARO");
        assertThat(apariencia.get("colorAcento").asText()).isEqualTo("#1FB6FF");
        enviar(put("/api/usuarios/me/apariencia"), ana, Map.of("tema", "CLARO", "colorAcento", "azul"), 400);
        enviar(put("/api/usuarios/me/apariencia"), null, Map.of("tema", "OSCURO", "colorAcento", "#000000"), 401);
        enviar(get("/api/admin/dashboard"), ana, null, 403);
        enviar(get("/api/admin/dashboard"), null, null, 401);

        // ---------- Jornadas e importación del CSV ----------
        long j1 = enviar(post("/api/admin/jornadas"), admin,
                Map.of("nombre", "Semana 1", "fechaInicio", "2026-01-17", "fechaFin", "2026-01-18"), 201).get("id").asLong();
        enviar(post("/api/admin/jornadas"), admin,
                Map.of("nombre", "Semana 2", "fechaInicio", "2026-01-24", "fechaFin", "2026-01-25"), 201);

        JsonNode imp = importarCsv(admin, j1);
        assertThat(imp.get("partidasImportadas").asInt()).isEqualTo(2);
        assertThat(imp.get("partidasFueraDeFecha").asInt()).isEqualTo(1);
        assertThat(imp.get("jugadoresCreados").asInt()).isEqualTo(10);
        assertThat(imp.get("equiposCreados").asInt()).isEqualTo(2);
        assertThat(importarCsv(admin, j1).get("partidasYaExistentes").asInt()).isEqualTo(2); // idempotente

        Map<String, Long> jug = new HashMap<>();
        for (JsonNode j : enviar(get("/api/jugadores"), null, null, 200)) {
            jug.put(j.get("nickname").asText(), j.get("id").asLong());
        }
        assertThat(jug).hasSize(10);

        // ---------- Liga privada ----------
        JsonNode liga = enviar(post("/api/ligas"), ana, Map.of("nombre", "Liga Amigos", "tipo", "PRIVADO",
                "presupuestoInicial", 50000000, "maxParticipantes", 10, "nombreEquipo", "Los de Ana"), 201);
        long ligaId = liga.get("id").asLong();
        long equipoAna = liga.get("miEquipoId").asLong();
        long equipoBea = enviar(post("/api/ligas/unirse"), bea,
                Map.of("codigo", liga.get("codigoInvitacion").asText(), "nombreEquipo", "Bea FC"), 200).get("miEquipoId").asLong();
        assertThat(enviar(get("/api/ligas/mias"), ana, null, 200)).hasSize(2); // Liga Global + Liga Amigos

        // ---------- Mercado ----------
        for (String nick : new String[]{"AlfaTop", "AlfaJng", "AlfaMid", "AlfaAdc", "AlfaSup"}) {
            fichar(ana, equipoAna, jug.get(nick), 200);
        }
        JsonNode equipo = fichar(ana, equipoAna, jug.get("BetaMid"), 200);
        assertThat(slotDe(equipo, "BetaMid")).isEqualTo("BANQUILLO_1");
        assertThat(equipo.get("dineroDisponible").decimalValue()).isEqualByComparingTo("2000000");
        fichar(ana, equipoAna, jug.get("BetaTop"), 409); // sin dinero
        fichar(ana, equipoAna, jug.get("AlfaTop"), 409); // repetido
        fichar(bea, equipoAna, jug.get("BetaTop"), 403); // equipo ajeno

        // Alineación: un MID puede jugar de MID o en el banquillo, nunca de TOP
        Map<String, Long> alineacion = new LinkedHashMap<>();
        alineacion.put("TOP", jug.get("AlfaTop"));
        alineacion.put("JNG", jug.get("AlfaJng"));
        alineacion.put("MID", jug.get("BetaMid"));
        alineacion.put("ADC", jug.get("AlfaAdc"));
        alineacion.put("SUP", jug.get("AlfaSup"));
        alineacion.put("BANQUILLO_1", jug.get("AlfaMid"));
        assertThat(slotDe(enviar(put("/api/equipos/" + equipoAna + "/alineacion"), ana, Map.of("slots", alineacion), 200), "BetaMid"))
                .isEqualTo("MID");
        alineacion.put("TOP", jug.get("BetaMid"));
        alineacion.put("MID", jug.get("AlfaTop"));
        enviar(put("/api/equipos/" + equipoAna + "/alineacion"), ana, Map.of("slots", alineacion), 400);
        alineacion.put("TOP", jug.get("AlfaTop"));
        alineacion.put("MID", jug.get("AlfaMid"));
        alineacion.put("BANQUILLO_1", jug.get("BetaMid"));
        enviar(put("/api/equipos/" + equipoAna + "/alineacion"), ana, Map.of("slots", alineacion), 200);

        // Mejora "Triple Kill" (x2) para AlfaMid, que hace una triple en la primera partida
        long tripleKill = 0;
        for (JsonNode m : enviar(get("/api/mejoras"), null, null, 200)) {
            if (m.get("condicion").asText().equals("TRIPLE_KILL")) {
                tripleKill = m.get("id").asLong();
            }
        }
        JsonNode mejora = enviar(post("/api/equipos/" + equipoAna + "/mejoras"), ana,
                Map.of("mejoraId", tripleKill, "jugadorId", jug.get("AlfaMid")), 201);
        assertThat(mejora.get("jornadaId").asLong()).isEqualTo(j1);

        fichar(bea, equipoBea, jug.get("BetaAdc"), 200);

        // ---------- Jornada en curso: mercado cerrado ----------
        assertThat(enviar(post("/api/admin/jornadas/" + j1 + "/iniciar"), admin, null, 200).get("estado").asText())
                .isEqualTo("EN_CURSO");
        fichar(bea, equipoBea, jug.get("BetaTop"), 409);

        Map<String, BigDecimal> puntos = new HashMap<>();
        JsonNode previa = enviar(get("/api/admin/jornadas/" + j1 + "/previsualizacion"), admin, null, 200);
        assertThat(previa.get("partidas").asInt()).isEqualTo(2);
        for (JsonNode p : previa.get("jugadores")) {
            puntos.put(p.get("nickname").asText(), p.get("puntos").decimalValue());
        }
        BigDecimal esperadoAna = puntos.get("AlfaTop").add(puntos.get("AlfaJng")).add(puntos.get("AlfaMid"))
                .add(puntos.get("AlfaAdc")).add(puntos.get("AlfaSup"))
                .add(puntos.get("AlfaMid")); // bonus de la mejora: x2 sobre AlfaMid

        JsonNode fin = enviar(post("/api/admin/jornadas/" + j1 + "/finalizar"), admin, null, 200);
        assertThat(fin.get("mejorasCumplidas").asInt()).isEqualTo(1);
        assertThat(fin.get("equiposPuntuados").asInt()).isGreaterThanOrEqualTo(5);

        JsonNode equipoFinal = enviar(get("/api/equipos/" + equipoAna), ana, null, 200);
        assertThat(equipoFinal.get("puntosTotales").decimalValue()).isEqualByComparingTo(esperadoAna);
        assertThat(equipoFinal.get("mercadoAbierto").asBoolean()).isTrue();
        JsonNode clasif = enviar(get("/api/ligas/" + ligaId + "/clasificacion"), bea, null, 200);
        assertThat(clasif.get(0).get("equipo").asText()).isEqualTo("Los de Ana");
        assertThat(enviar(get("/api/ligas/" + ligaId + "/clasificacion?jornadaId=" + j1), bea, null, 200)).hasSize(2);
        // La clasificación trae el valor de la plantilla, los puntos de la última jornada y la racha
        assertThat(clasif.get(0).get("valorPlantilla").decimalValue()).isPositive();
        assertThat(clasif.get(0).get("puntosUltimaJornada").decimalValue()).isEqualByComparingTo(esperadoAna);
        assertThat(clasif.get(0).get("racha")).hasSize(1);
        assertThat(clasif.get(0).get("racha").get(0).get("porEncimaDeLaMedia").asBoolean()).isTrue();

        // Al cerrar la jornada cambian los precios: queda el valor anterior y el historial para la gráfica
        JsonNode fichaMid = enviar(get("/api/jugadores/" + jug.get("AlfaMid")), null, null, 200);
        JsonNode historialMid = fichaMid.get("historialValor");
        assertThat(historialMid).hasSize(2);
        assertThat(historialMid.get(0).get("valor").decimalValue())
                .isEqualByComparingTo(fichaMid.get("jugador").get("valorAnterior").decimalValue());
        assertThat(historialMid.get(1).get("valor").decimalValue())
                .isEqualByComparingTo(fichaMid.get("jugador").get("valorMercado").decimalValue());
        assertThat(historialMid.get(1).get("jornadaId").asLong()).isEqualTo(j1);
        assertThat(enviar(get("/api/equipos/" + equipoAna + "/mejoras"), ana, null, 200).get(0).get("estado").asText())
                .isEqualTo("CUMPLIDA");
        enviar(get("/api/equipos/" + equipoAna), admin, null, 200);        // el admin puede verlo
        enviar(get("/api/ligas/" + ligaId), registrarYToken("intruso"), null, 403); // liga privada

        // ---------- Moderación: baneo ----------
        long beaId = enviar(get("/api/usuarios/me"), bea, null, 200).get("id").asLong();
        enviar(post("/api/admin/usuarios/" + beaId + "/baneo"), admin,
                Map.of("duracion", "D7", "motivo", "TOXICIDAD", "comentario", "insultos", "notificar", true), 200);
        enviar(get("/api/usuarios/me"), bea, null, 401);
        enviar(post("/api/auth/login"), null, Map.of("identificador", "bea_test", "password", "password123"), 403);
        JsonNode baneados = enviar(get("/api/admin/usuarios?estado=BANEADO"), admin, null, 200);
        assertThat(baneados.get("totalElementos").asInt()).isEqualTo(1);
        enviar(delete("/api/admin/usuarios/" + beaId + "/baneo"), admin, null, 200);
        enviar(get("/api/usuarios/me"), bea, null, 200);

        // ---------- Bloqueo de un jugador: sale de los equipos con reembolso ----------
        BigDecimal dineroAntes = equipoFinal.get("dineroDisponible").decimalValue();
        BigDecimal valorAlfaTop = enviar(get("/api/jugadores/" + jug.get("AlfaTop")), null, null, 200)
                .get("jugador").get("valorMercado").decimalValue();
        enviar(post("/api/admin/jugadores/" + jug.get("AlfaTop") + "/bloqueo"), admin, Map.of("motivo", "Sancionado"), 200);
        JsonNode trasBloqueo = enviar(get("/api/equipos/" + equipoAna), ana, null, 200);
        assertThat(trasBloqueo.get("plantilla")).hasSize(5);
        assertThat(trasBloqueo.get("dineroDisponible").decimalValue()).isEqualByComparingTo(dineroAntes.add(valorAlfaTop));
        assertThat(enviar(get("/api/jugadores"), null, null, 200)).hasSize(9);
        fichar(ana, equipoAna, jug.get("AlfaTop"), 409);

        // ---------- Login con Google ----------
        when(google.verificar(anyString()))
                .thenReturn(new GoogleTokenVerifier.GoogleUser("g-123", "carla@gmail.com", "Carla Gómez"));
        JsonNode carla = enviar(post("/api/auth/google"), null, Map.of("idToken", "token-falso"), 200);
        assertThat(carla.get("usuario").get("perfilCompleto").asBoolean()).isFalse();
        assertThat(carla.get("usuario").get("tienePassword").asBoolean()).isFalse();
        String carlaToken = token(carla);
        assertThat(enviar(put("/api/usuarios/me"), carlaToken, Map.of("fechaNacimiento", "2000-05-01", "pais", "es"), 200)
                .get("perfilCompleto").asBoolean()).isTrue();

        // ---------- Reportes ----------
        long reporte = enviar(post("/api/reportes"), ana, Map.of("usuarioReportadoId", beaId, "motivo", "TOXICIDAD",
                "descripcion", "Spam en el chat"), 201).get("id").asLong();
        assertThat(enviar(get("/api/admin/reportes?estado=PENDIENTE"), admin, null, 200)).hasSize(1);
        enviar(post("/api/admin/reportes/" + reporte + "/resolver"), admin, Map.of("estado", "RESUELTO"), 200);

        // ---------- Noticias ----------
        enviar(post("/api/admin/noticias"), admin,
                Map.of("titulo", "Arranca la temporada", "contenido", "Ya puedes fichar.", "publicada", true), 201);
        assertThat(enviar(get("/api/noticias"), null, null, 200).get("totalElementos").asInt()).isEqualTo(1);

        // ---------- Panel de control ----------
        JsonNode dashboard = enviar(get("/api/admin/dashboard"), admin, null, 200);
        assertThat(dashboard.get("usuarios").asInt()).isGreaterThanOrEqualTo(5);
        assertThat(dashboard.get("jugadoresBloqueados").asInt()).isEqualTo(1);
        assertThat(dashboard.get("reportesPendientes").asInt()).isZero();
        assertThat(dashboard.get("actividadReciente")).isNotEmpty();

        // ---------- Eliminar usuario ----------
        enviar(delete("/api/admin/usuarios/" + beaId), admin, null, 204);
        enviar(get("/api/usuarios/me"), bea, null, 401);
        assertThat(enviar(get("/api/ligas/" + ligaId + "/clasificacion"), ana, null, 200)).hasSize(1);
    }

    // ------------------------------------------------------------------

    private Map<String, Object> registro(String nombre, String email) {
        Map<String, Object> m = new HashMap<>();
        m.put("nombre", nombre);
        m.put("email", email);
        m.put("password", "password123");
        m.put("repetirPassword", "password123");
        m.put("fechaNacimiento", "2001-03-15");
        m.put("pais", "ES");
        m.put("aceptaTerminos", true);
        return m;
    }

    private JsonNode registrar(String nombre, String email) throws Exception {
        JsonNode r = enviar(post("/api/auth/registro"), null, registro(nombre, email), 201);
        assertThat(r.get("usuario").get("perfilCompleto").asBoolean()).isTrue();
        return r;
    }

    private String registrarYToken(String nombre) throws Exception {
        return token(registrar(nombre, nombre + "@test.com"));
    }

    private static String token(JsonNode authResponse) {
        return authResponse.get("token").asText();
    }

    private JsonNode fichar(String token, long equipoId, long jugadorId, int estado) throws Exception {
        return enviar(post("/api/equipos/" + equipoId + "/fichajes"), token, Map.of("jugadorId", jugadorId), estado);
    }

    private static String slotDe(JsonNode equipo, String nickname) {
        for (JsonNode p : equipo.get("plantilla")) {
            if (p.get("jugador").get("nickname").asText().equals(nickname)) {
                return p.get("slot").asText();
            }
        }
        return null;
    }

    private JsonNode importarCsv(String token, long jornadaId) throws Exception {
        byte[] csv;
        try (InputStream in = getClass().getResourceAsStream("/datos/oe-prueba.csv")) {
            csv = in.readAllBytes();
        }
        MvcResult r = mvc.perform(multipart("/api/admin/jornadas/" + jornadaId + "/importar/csv")
                        .file(new MockMultipartFile("archivo", "oe.csv", "text/csv", csv))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return om.readTree(r.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private JsonNode enviar(MockHttpServletRequestBuilder peticion, String token, Object cuerpo, int estadoEsperado)
            throws Exception {
        if (token != null) {
            peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        if (cuerpo != null) {
            peticion.contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(cuerpo));
        }
        MvcResult r = mvc.perform(peticion).andReturn();
        String body = r.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(r.getResponse().getStatus())
                .as("%s %s → %s", r.getRequest().getMethod(), r.getRequest().getRequestURI(), body)
                .isEqualTo(estadoEsperado);
        return body.isBlank() ? om.createObjectNode() : om.readTree(body);
    }
}
