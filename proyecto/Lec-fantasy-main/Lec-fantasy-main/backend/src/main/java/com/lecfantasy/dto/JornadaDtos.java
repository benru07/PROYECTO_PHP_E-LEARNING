package com.lecfantasy.dto;

import com.lecfantasy.domain.EstadoJornada;
import com.lecfantasy.domain.Jornada;
import com.lecfantasy.domain.PartidaReal;
import com.lecfantasy.domain.Posicion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class JornadaDtos {

    private JornadaDtos() {
    }

    public record JornadaDto(Long id, String nombre, LocalDate fechaInicio, LocalDate fechaFin, EstadoJornada estado,
                             String fuenteOverview, LocalDateTime fechaFinalizacion) {

        public static JornadaDto of(Jornada j) {
            return new JornadaDto(j.getId(), j.getNombre(), j.getFechaInicio(), j.getFechaFin(), j.getEstado(),
                    j.getFuenteOverview(), j.getFechaFinalizacion());
        }
    }

    public record JornadaRequest(
            @NotBlank @Size(max = 100) String nombre,
            @NotNull LocalDate fechaInicio,
            @NotNull LocalDate fechaFin,
            @Size(max = 200) String fuenteOverview) {
    }

    public record PartidaDto(Long id, String idExterno, LocalDateTime fechaHora, CatalogoDtos.EquipoProDto equipoAzul,
                             CatalogoDtos.EquipoProDto equipoRojo, CatalogoDtos.EquipoProDto ganador,
                             Integer duracionSegundos) {

        public static PartidaDto of(PartidaReal p) {
            return new PartidaDto(p.getId(), p.getIdExterno(), p.getFechaHora(),
                    CatalogoDtos.EquipoProDto.ofNullable(p.getEquipoAzul()),
                    CatalogoDtos.EquipoProDto.ofNullable(p.getEquipoRojo()),
                    CatalogoDtos.EquipoProDto.ofNullable(p.getGanador()), p.getDuracionSegundos());
        }
    }

    public record PartidaDetalleDto(PartidaDto partida, List<CatalogoDtos.EstadisticaDto> estadisticas) {
    }

    public record ImportResultado(int partidasImportadas, int partidasYaExistentes, int partidasFueraDeFecha,
                                  int jugadoresCreados, int equiposCreados, int lineasIgnoradas,
                                  List<String> avisos) {
    }

    /** Vista previa para que el admin valide los puntos antes de cerrar la jornada. */
    public record PrevisualizacionDto(JornadaDto jornada, int partidas, List<PuntosJugadorDto> jugadores) {
    }

    public record PuntosJugadorDto(Long jugadorId, String nickname, Posicion posicion, String equipo, int partidas,
                                   BigDecimal puntos) {
    }

    public record FinalizacionResultado(JornadaDto jornada, int equiposPuntuados, int mejorasCumplidas,
                                        int mejorasFallidas, int preciosActualizados) {
    }
}
