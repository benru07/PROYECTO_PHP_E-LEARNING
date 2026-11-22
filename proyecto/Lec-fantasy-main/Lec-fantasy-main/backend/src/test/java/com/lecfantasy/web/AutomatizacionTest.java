package com.lecfantasy.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lecfantasy.service.importer.LeaguepediaClient;
import com.lecfantasy.service.importer.OraclesElixirParser;
import com.lecfantasy.service.importer.StatLine;
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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Gestión automática de jornadas: la tarea programada inicia la jornada en su fecha y, cuando
 * ha terminado, importa las partidas de Leaguepedia (aquí simulada con el CSV de prueba) y la cierra.
 * Usa su propia base de datos en memoria para no mezclarse con FlujoCompletoTest.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url="
        + "jdbc:h2:mem:automatizacion;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1")
class AutomatizacionTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OraclesElixirParser parser;

    @MockitoBean
    private LeaguepediaClient leaguepedia;

    @Test
    void iniciaImportaYCierraJornadasSolas() throws Exception {
        String admin = enviar(post("/api/auth/login"), null,
                Map.of("identificador", "admin@test.com", "password", "admin12345"), 200).get("token").asText();

        // Jornada ya pasada (las fechas del CSV de prueba) y otra en el futuro
        long j1 = enviar(post("/api/admin/jornadas"), admin,
                Map.of("nombre", "Semana 1", "fechaInicio", "2026-01-17", "fechaFin", "2026-01-18"), 201).get("id").asLong();
        LocalDate futuro = LocalDate.now().plusDays(30);
        long j2 = enviar(post("/api/admin/jornadas"), admin, Map.of("nombre", "Semana futura",
                "fechaInicio", futuro.toString(), "fechaFin", futuro.plusDays(1).toString()), 201).get("id").asLong();

        assertThat(enviar(get("/api/admin/automatizacion"), admin, null, 200).get("cronImportacion").asText()).isEqualTo("-");

        // 1ª ejecución: la jornada pasada se inicia (mercado cerrado); la futura no se toca
        JsonNode ejecucion = enviar(post("/api/admin/automatizacion/ejecutar"), admin, null, 200);
        assertThat(ejecucion.get("acciones").toString()).contains("Semana 1").contains("iniciada");
        assertThat(estado(j1)).isEqualTo("EN_CURSO");
        assertThat(estado(j2)).isEqualTo("PROGRAMADA");

        // 2ª ejecución sin datos en Leaguepedia: se queda en curso para reintentarlo
        when(leaguepedia.obtener(any(), any(), any())).thenReturn(List.of());
        ejecucion = enviar(post("/api/admin/automatizacion/ejecutar"), admin, null, 200);
        assertThat(ejecucion.get("acciones").toString()).contains("se reintentará");
        assertThat(estado(j1)).isEqualTo("EN_CURSO");

        // 3ª ejecución con las partidas publicadas: importa y finaliza
        when(leaguepedia.obtener(any(), any(), any())).thenReturn(lineasDePrueba());
        ejecucion = enviar(post("/api/admin/automatizacion/ejecutar"), admin, null, 200);
        assertThat(ejecucion.get("acciones").toString()).contains("2 partidas nuevas").contains("finalizada");
        assertThat(estado(j1)).isEqualTo("FINALIZADA");
        assertThat(estado(j2)).isEqualTo("PROGRAMADA");
        assertThat(enviar(get("/api/jornadas/" + j1 + "/partidas"), null, null, 200)).hasSize(2);
        assertThat(enviar(get("/api/admin/automatizacion"), admin, null, 200).get("ultima").get("manual").asBoolean()).isTrue();

        // Queda en el registro de actividad
        JsonNode actividad = enviar(get("/api/admin/actividad"), admin, null, 200).get("contenido");
        assertThat(actividad.toString()).contains("El sistema (automático) finalizó la jornada «Semana 1»");
    }

    private List<StatLine> lineasDePrueba() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/datos/oe-prueba.csv")) {
            return parser.leer(in, "LEC").lineas();
        }
    }

    private String estado(long jornadaId) throws Exception {
        for (JsonNode j : enviar(get("/api/jornadas"), null, null, 200)) {
            if (j.get("id").asLong() == jornadaId) {
                return j.get("estado").asText();
            }
        }
        return null;
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
