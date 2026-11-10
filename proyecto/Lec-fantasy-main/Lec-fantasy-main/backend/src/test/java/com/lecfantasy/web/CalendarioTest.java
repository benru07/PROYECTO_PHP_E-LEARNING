package com.lecfantasy.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lecfantasy.service.calendario.PandaScoreClient;
import com.lecfantasy.service.calendario.PartidoCalendario;
import com.lecfantasy.service.calendario.PartidoCalendario.Equipo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Calendario de PandaScore (simulado): crea una jornada por semana con partidos, no la duplica,
 * copia logos y siglas a los equipos y sirve los próximos partidos sin exponer la key.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url="
        + "jdbc:h2:mem:calendario;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1")
class CalendarioTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private PandaScoreClient pandascore;

    @Test
    void creaJornadasDesdeElCalendario() throws Exception {
        String admin = enviar(post("/api/auth/login"), null,
                Map.of("identificador", "admin@test.com", "password", "admin12345"), 200).get("token").asText();
        enviar(post("/api/admin/equipos-pro"), admin, Map.of("nombre", "G2 Esports"), 201);

        // Dos fines de semana con partidos: el sábado y el domingo de la semana que viene y el sábado siguiente
        LocalDate sabado = LocalDate.now(MADRID).plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        Equipo g2 = new Equipo(10, "G2 Esports", "G2", "https://cdn/g2.png");
        Equipo fnc = new Equipo(20, "Fnatic", "FNC", null);
        List<PartidoCalendario> partidos = List.of(
                partido(1, sabado, g2, fnc), partido(2, sabado.plusDays(1), fnc, g2), partido(3, sabado.plusWeeks(1), g2, fnc));
        when(pandascore.configurado()).thenReturn(true);
        when(pandascore.partidos(any(), any())).thenReturn(partidos);

        assertThat(enviar(get("/api/admin/automatizacion"), admin, null, 200).get("calendario").asBoolean()).isTrue();
        JsonNode ejecucion = enviar(post("/api/admin/automatizacion/ejecutar"), admin, null, 200);
        assertThat(ejecucion.get("acciones").toString()).contains("creada desde el calendario de PandaScore");

        JsonNode jornadas = enviar(get("/api/jornadas"), null, null, 200);
        assertThat(jornadas).hasSize(2);
        assertThat(jornadas.get(0).get("fechaInicio").asText()).isEqualTo(sabado.toString());
        assertThat(jornadas.get(0).get("fechaFin").asText()).isEqualTo(sabado.plusDays(1).toString());
        assertThat(jornadas.get(0).get("estado").asText()).isEqualTo("PROGRAMADA");
        assertThat(jornadas.get(0).get("nombre").asText()).isEqualTo("Jornada 1 · Versus 2027");
        assertThat(jornadas.get(1).get("fechaInicio").asText()).isEqualTo(sabado.plusWeeks(1).toString());

        // Volver a ejecutarlo no duplica jornadas
        enviar(post("/api/admin/automatizacion/ejecutar"), admin, null, 200);
        assertThat(enviar(get("/api/jornadas"), null, null, 200)).hasSize(2);

        // El logo de PandaScore pasa al equipo que no tenía
        JsonNode equipo = enviar(get("/api/equipos-pro"), null, null, 200).get(0);
        assertThat(equipo.get("logoUrl").asText()).isEqualTo("https://cdn/g2.png");
        assertThat(equipo.get("siglas").asText()).isEqualTo("G2");

        // Próximos partidos, públicos y sin rastro de la key
        MvcResult r = mvc.perform(get("/api/calendario")).andReturn();
        String cuerpo = r.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(r.getResponse().getStatus()).isEqualTo(200);
        assertThat(om.readTree(cuerpo)).hasSize(3);
        assertThat(cuerpo).doesNotContainIgnoringCase("token");
    }

    private static PartidoCalendario partido(long id, LocalDate dia, Equipo a, Equipo b) {
        Instant hora = dia.atTime(18, 0).atZone(MADRID).toInstant();
        return new PartidoCalendario(id, hora, "not_started", a, b, null, null, null, 1, "Versus 2027");
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
