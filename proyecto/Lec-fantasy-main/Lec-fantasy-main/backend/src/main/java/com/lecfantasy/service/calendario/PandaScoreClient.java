package com.lecfantasy.service.calendario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lecfantasy.common.ApiException;
import com.lecfantasy.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Cliente de la API REST de PandaScore (https://developers.pandascore.co).
 * Con el plan gratuito ("Fixtures Only") da el calendario y los resultados de los partidos,
 * pero no las estadísticas de cada jugador: los puntos siguen saliendo de Leaguepedia u Oracle's Elixir.
 * El token se lee de la variable de entorno PANDASCORE_TOKEN y nunca se envía al navegador.
 */
@Component
public class PandaScoreClient {

    private static final Logger log = LoggerFactory.getLogger(PandaScoreClient.class);
    private static final int POR_PAGINA = 100;
    private static final int MAX_PAGINAS = 5;

    private final RestClient rest;
    private final ObjectMapper mapper;
    private final AppProperties.PandaScore config;
    private volatile Long ligaId;

    public PandaScoreClient(ObjectMapper mapper, AppProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(30_000);
        this.rest = RestClient.builder().requestFactory(factory).build();
        this.mapper = mapper;
        this.config = props.pandascore();
        this.ligaId = config.ligaId();
    }

    /** true si hay token configurado. */
    public boolean configurado() {
        return config.token() != null && !config.token().isBlank();
    }

    /** Partidos de la LEC programados entre dos instantes, ordenados por fecha. */
    public List<PartidoCalendario> partidos(Instant desde, Instant hasta) {
        long liga = ligaId();
        // PandaScore espera fechas ISO sin fracciones de segundo
        desde = desde.truncatedTo(ChronoUnit.SECONDS);
        hasta = hasta.truncatedTo(ChronoUnit.SECONDS);
        List<PartidoCalendario> lista = new ArrayList<>();
        for (int pagina = 1; pagina <= MAX_PAGINAS; pagina++) {
            URI uri = UriComponentsBuilder.fromUriString(config.url()).path("/lol/matches")
                    .queryParam("filter[league_id]", liga)
                    .queryParam("range[scheduled_at]", desde + "," + hasta)
                    .queryParam("sort", "scheduled_at")
                    .queryParam("per_page", POR_PAGINA)
                    .queryParam("page", pagina)
                    .encode().build().toUri();
            JsonNode nodos = pedir(uri);
            nodos.forEach(n -> {
                PartidoCalendario p = convertir(n);
                if (p != null) {
                    lista.add(p);
                }
            });
            if (nodos.size() < POR_PAGINA) {
                break;
            }
        }
        return lista;
    }

    /** Id de la LEC en PandaScore: el de PANDASCORE_LIGA_ID o, si no se indica, se busca por nombre. */
    long ligaId() {
        if (ligaId != null) {
            return ligaId;
        }
        URI uri = UriComponentsBuilder.fromUriString(config.url()).path("/lol/leagues")
                .queryParam("search[name]", config.ligaNombre())
                .queryParam("per_page", 50)
                .encode().build().toUri();
        String buscado = config.ligaNombre().toLowerCase(Locale.ROOT);
        JsonNode encontrada = null;
        for (JsonNode l : pedir(uri)) {
            if (l.path("name").asText("").equalsIgnoreCase(buscado)) {
                encontrada = l;
                break;
            }
        }
        if (encontrada == null) {
            throw new ApiException(HttpStatus.BAD_GATEWAY,
                    "PandaScore no tiene ninguna liga llamada «" + config.ligaNombre() + "». Indica PANDASCORE_LIGA_ID.");
        }
        ligaId = encontrada.path("id").asLong();
        log.info("PandaScore: liga {} con id {}", config.ligaNombre(), ligaId);
        return ligaId;
    }

    static PartidoCalendario convertir(JsonNode n) {
        JsonNode rivales = n.path("opponents");
        if (rivales.size() < 2) {
            return null; // partido aún sin rivales definidos (p. ej. playoffs por decidir)
        }
        PartidoCalendario.Equipo a = equipo(rivales.get(0).path("opponent"));
        PartidoCalendario.Equipo b = equipo(rivales.get(1).path("opponent"));
        Integer marcadorA = null;
        Integer marcadorB = null;
        for (JsonNode r : n.path("results")) {
            long equipo = r.path("team_id").asLong(-1);
            if (equipo == a.id()) {
                marcadorA = r.path("score").asInt();
            } else if (equipo == b.id()) {
                marcadorB = r.path("score").asInt();
            }
        }
        String fecha = texto(n, "scheduled_at");
        if (fecha == null) {
            fecha = texto(n, "begin_at");
        }
        if (fecha == null) {
            return null;
        }
        String competicion = texto(n.path("serie"), "full_name");
        if (competicion == null) {
            competicion = texto(n.path("tournament"), "name");
        }
        return new PartidoCalendario(n.path("id").asLong(), Instant.parse(fecha), n.path("status").asText("not_started"),
                a, b, marcadorA, marcadorB, n.hasNonNull("winner_id") ? n.get("winner_id").asLong() : null,
                n.path("number_of_games").asInt(1), competicion);
    }

    private static PartidoCalendario.Equipo equipo(JsonNode o) {
        return new PartidoCalendario.Equipo(o.path("id").asLong(), o.path("name").asText("?"),
                texto(o, "acronym"), texto(o, "image_url"));
    }

    private static String texto(JsonNode n, String campo) {
        JsonNode v = n.path(campo);
        return v.isMissingNode() || v.isNull() || v.asText().isBlank() ? null : v.asText();
    }

    private JsonNode pedir(URI uri) {
        if (!configurado()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Falta la variable de entorno PANDASCORE_TOKEN");
        }
        try {
            String cuerpo = rest.get().uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + config.token())
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .retrieve()
                    .body(String.class);
            JsonNode raiz = mapper.readTree(cuerpo == null ? "[]" : cuerpo);
            if (!raiz.isArray()) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Respuesta de PandaScore no válida");
            }
            return raiz;
        } catch (RestClientException e) {
            // Sin el token en el mensaje: la URL no lo lleva (va en la cabecera)
            throw new ApiException(HttpStatus.BAD_GATEWAY, "No se pudo consultar PandaScore: " + e.getMessage());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Respuesta de PandaScore no válida");
        }
    }
}
