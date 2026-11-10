package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.Posicion;
import com.lecfantasy.domain.Slot;
import com.lecfantasy.service.importer.OraclesElixirParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReglasPlantillaTest {

    @Test
    void unFichajeVaASuPuestoYSiEstaOcupadoAlBanquillo() {
        assertThat(EquipoService.huecoLibre(Posicion.MID, EnumSet.noneOf(Slot.class))).isEqualTo(Slot.MID);
        assertThat(EquipoService.huecoLibre(Posicion.MID, EnumSet.of(Slot.MID))).isEqualTo(Slot.BANQUILLO_1);
        assertThat(EquipoService.huecoLibre(Posicion.MID, EnumSet.of(Slot.MID, Slot.BANQUILLO_1))).isEqualTo(Slot.BANQUILLO_2);
        assertThatThrownBy(() -> EquipoService.huecoLibre(Posicion.MID, EnumSet.of(Slot.MID, Slot.BANQUILLO_1, Slot.BANQUILLO_2)))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void losHuecosDeTitularSoloAdmitenSuRol() {
        assertThat(Slot.TOP.admite(Posicion.TOP)).isTrue();
        assertThat(Slot.TOP.admite(Posicion.MID)).isFalse();
        assertThat(Slot.BANQUILLO_2.admite(Posicion.SUP)).isTrue();
    }

    @Test
    void rolesExternos() {
        assertThat(Posicion.desdeTextoExterno("Jungle")).isEqualTo(Posicion.JNG);
        assertThat(Posicion.desdeTextoExterno("bot")).isEqualTo(Posicion.ADC);
        assertThat(Posicion.desdeTextoExterno("Support")).isEqualTo(Posicion.SUP);
        assertThat(Posicion.desdeTextoExterno("team")).isNull();
    }

    @Test
    void leeElCsvDeOraclesElixirFiltrandoLiga() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/datos/oe-prueba.csv")) {
            OraclesElixirParser.Resultado r = new OraclesElixirParser().leer(in, "LEC");
            assertThat(r.lineas()).hasSize(30); // 3 partidas LEC x 10 jugadores; la de LCK y las filas "team" se descartan
            assertThat(r.lineas().get(0).partidaId()).isEqualTo("OE:ESPORTSTMNT01_0001");
            assertThat(r.lineas().get(0).ladoAzul()).isTrue();
            assertThat(r.lineas().get(2).tripleKills()).isEqualTo(1);
            assertThat(r.lineas().get(1).firstBlood()).isTrue();
            assertThat(r.lineas().get(0).cs()).isEqualTo(280);
        }
    }
}
