package com.lecfantasy.service.calendario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/** Conversión de la respuesta de /lol/matches de PandaScore (formato de su documentación). */
class PandaScoreClientTest {

    private static final String PARTIDO = """
            {
              "id": 1234, "name": "G2 vs FNC", "status": "finished",
              "scheduled_at": "2027-01-17T16:00:00Z", "begin_at": "2027-01-17T16:04:00Z",
              "number_of_games": 3, "match_type": "best_of", "winner_id": 10, "winner_type": "Team",
              "opponents": [
                {"type": "Team", "opponent": {"id": 10, "name": "G2 Esports", "acronym": "G2", "image_url": "https://cdn/g2.png"}},
                {"type": "Team", "opponent": {"id": 20, "name": "Fnatic", "acronym": "FNC", "image_url": null}}
              ],
              "results": [{"team_id": 20, "score": 1}, {"team_id": 10, "score": 2}],
              "serie": {"full_name": "Versus 2027", "name": "Versus"},
              "tournament": {"name": "Regular Season"}
            }""";

    @Test
    void convierteUnPartido() throws Exception {
        JsonNode n = new ObjectMapper().readTree(PARTIDO);
        PartidoCalendario p = PandaScoreClient.convertir(n);
        assertThat(p).isNotNull();
        assertThat(p.fecha()).isEqualTo(Instant.parse("2027-01-17T16:00:00Z"));
        assertThat(p.equipoA().siglas()).isEqualTo("G2");
        assertThat(p.equipoA().logoUrl()).isEqualTo("https://cdn/g2.png");
        assertThat(p.equipoB().logoUrl()).isNull();
        assertThat(p.marcadorA()).isEqualTo(2);
        assertThat(p.marcadorB()).isEqualTo(1);
        assertThat(p.ganadorId()).isEqualTo(10L);
        assertThat(p.mejorDe()).isEqualTo(3);
        assertThat(p.competicion()).isEqualTo("Versus 2027");
    }

    @Test
    void ignoraPartidosSinRivales() throws Exception {
        JsonNode n = new ObjectMapper().readTree("""
                {"id": 1, "status": "not_started", "scheduled_at": "2027-03-01T16:00:00Z", "opponents": [], "results": []}""");
        assertThat(PandaScoreClient.convertir(n)).isNull();
    }
}
