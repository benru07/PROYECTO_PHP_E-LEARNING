package com.lecfantasy.service;

import com.lecfantasy.domain.CondicionMejora;
import com.lecfantasy.domain.EstadisticaJugadorPartida;
import com.lecfantasy.domain.Mejora;
import com.lecfantasy.service.importer.ImportService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PuntuacionTest {

    private static EstadisticaJugadorPartida linea(int k, int d, int a, int cs, int vision, boolean victoria) {
        EstadisticaJugadorPartida e = new EstadisticaJugadorPartida();
        e.setKills(k);
        e.setDeaths(d);
        e.setAssists(a);
        e.setCs(cs);
        e.setVisionScore(vision);
        e.setVictoria(victoria);
        return e;
    }

    @Test
    void ejemploDeLaDocumentacion() {
        // ADC 6/2/8, 300 CS, 30 de visión y victoria = 41,5
        assertThat(Puntuacion.base(linea(6, 2, 8, 300, 30, true))).isEqualByComparingTo("41.5");
    }

    @Test
    void multikillsYFirstBloodSuman() {
        EstadisticaJugadorPartida e = linea(0, 0, 0, 0, 0, false);
        e.setDoubleKills(1);
        e.setTripleKills(1);
        e.setQuadraKills(1);
        e.setPentaKills(1);
        e.setFirstBlood(true);
        assertThat(Puntuacion.base(e)).isEqualByComparingTo("20"); // 1 + 2 + 5 + 10 + 2
    }

    @Test
    void elMvpEsElDeMasPuntosYSumaTres() {
        EstadisticaJugadorPartida flojo = linea(1, 3, 2, 100, 10, false);
        EstadisticaJugadorPartida bueno = linea(8, 0, 4, 300, 20, true);
        ImportService.asignarMvpYPuntos(List.of(flojo, bueno));
        assertThat(bueno.isMvp()).isTrue();
        assertThat(flojo.isMvp()).isFalse();
        assertThat(bueno.getPuntosFantasy()).isEqualByComparingTo(Puntuacion.base(bueno).add(Puntuacion.MVP));
    }

    @Test
    void evaluadorDeMejoras() {
        Mejora triple = new Mejora();
        triple.setCondicion(CondicionMejora.TRIPLE_KILL);
        Mejora kda = new Mejora();
        kda.setCondicion(CondicionMejora.KDA_MINIMO);
        kda.setUmbral(new BigDecimal("5"));
        Mejora inmortal = new Mejora();
        inmortal.setCondicion(CondicionMejora.CERO_MUERTES);

        EstadisticaJugadorPartida conQuadra = linea(5, 1, 5, 0, 0, true);
        conQuadra.setQuadraKills(1);
        EstadisticaJugadorPartida normal = linea(2, 1, 1, 0, 0, true);

        assertThat(EvaluadorMejoras.cumple(triple, List.of(conQuadra))).isTrue();   // una quadra cuenta como triple
        assertThat(EvaluadorMejoras.cumple(triple, List.of(normal))).isFalse();
        assertThat(EvaluadorMejoras.cumple(kda, List.of(conQuadra))).isTrue();      // (5+5)/1 = 10
        assertThat(EvaluadorMejoras.cumple(kda, List.of(normal))).isFalse();        // (2+1)/1 = 3
        assertThat(EvaluadorMejoras.cumple(inmortal, List.of(normal))).isFalse();
        assertThat(EvaluadorMejoras.cumple(triple, List.of())).isFalse();          // si no juega, falla
    }
}
