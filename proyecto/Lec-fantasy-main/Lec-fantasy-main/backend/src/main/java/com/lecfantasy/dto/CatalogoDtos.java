package com.lecfantasy.dto;

import com.lecfantasy.domain.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Equipos profesionales, jugadores reales y mejoras. */
public final class CatalogoDtos {

    private CatalogoDtos() {
    }

    public record EquipoProDto(Long id, String nombre, String siglas, String logoUrl) {

        public static EquipoProDto ofNullable(EquipoPro e) {
            return e == null ? null : new EquipoProDto(e.getId(), e.getNombre(), e.getSiglas(), e.getLogoUrl());
        }
    }

    public record EquipoProRequest(@NotBlank @Size(max = 100) String nombre, @Size(max = 10) String siglas,
                                   @Size(max = 500) String logoUrl) {
    }

    public record JugadorDto(Long id, String nickname, String nombreReal, Posicion posicion, EquipoProDto equipo,
                             BigDecimal valorMercado, BigDecimal valorAnterior, String fotoUrl, boolean bloqueado,
                             String motivoBloqueo, BigDecimal puntosTotales, long partidasJugadas) {

        public static JugadorDto of(JugadorReal j, BigDecimal puntos, long partidas) {
            return new JugadorDto(j.getId(), j.getNickname(), j.getNombreReal(), j.getPosicion(),
                    EquipoProDto.ofNullable(j.getEquipoPro()), j.getValorMercado(), j.getValorAnterior(), j.getFotoUrl(),
                    j.isBloqueado(), j.getMotivoBloqueo(), puntos == null ? BigDecimal.ZERO : puntos, partidas);
        }
    }

    public record JugadorDetalleDto(JugadorDto jugador, List<EstadisticaDto> historial,
                                    List<ValorHistoricoDto> historialValor) {
    }

    /** Un punto de la gráfica de evolución del precio. */
    public record ValorHistoricoDto(LocalDateTime fecha, Long jornadaId, String jornada, BigDecimal valor) {

        public static ValorHistoricoDto of(HistorialValor h) {
            return new ValorHistoricoDto(h.getFecha(), h.getJornada() == null ? null : h.getJornada().getId(),
                    h.getJornada() == null ? null : h.getJornada().getNombre(), h.getValor());
        }
    }

    public record EstadisticaDto(Long partidaId, Long jornadaId, String jornada, LocalDateTime fecha,
                                 String jugador, Long jugadorId, Posicion posicion, String equipo, String campeon,
                                 int kills, int deaths, int assists, int cs, int visionScore,
                                 int doubleKills, int tripleKills, int quadraKills, int pentaKills,
                                 boolean firstBlood, boolean victoria, boolean mvp, BigDecimal puntos) {

        public static EstadisticaDto of(EstadisticaJugadorPartida e) {
            PartidaReal p = e.getPartida();
            return new EstadisticaDto(p.getId(), p.getJornada().getId(), p.getJornada().getNombre(), p.getFechaHora(),
                    e.getJugador().getNickname(), e.getJugador().getId(), e.getJugador().getPosicion(),
                    e.getEquipoPro() == null ? null : e.getEquipoPro().getNombre(), e.getCampeon(),
                    e.getKills(), e.getDeaths(), e.getAssists(), e.getCs(), e.getVisionScore(),
                    e.getDoubleKills(), e.getTripleKills(), e.getQuadraKills(), e.getPentaKills(),
                    e.isFirstBlood(), e.isVictoria(), e.isMvp(), e.getPuntosFantasy());
        }
    }

    public record JugadorRequest(
            @NotBlank @Size(max = 40) String nickname,
            @Size(max = 100) String nombreReal,
            @NotNull Posicion posicion,
            Long equipoId,
            @NotNull @DecimalMin(value = "100000", message = "Valor mínimo 100.000") BigDecimal valorMercado,
            @Size(max = 500) String fotoUrl) {
    }

    public record BloqueoRequest(@NotBlank(message = "Indica el motivo") @Size(max = 255) String motivo) {
    }

    public record MejoraDto(Long id, String nombre, String descripcion, BigDecimal precio, int multiplicadorBono,
                            CondicionMejora condicion, BigDecimal umbral, boolean activa) {

        public static MejoraDto of(Mejora m) {
            return new MejoraDto(m.getId(), m.getNombre(), m.getDescripcion(), m.getPrecio(),
                    m.getMultiplicadorBono(), m.getCondicion(), m.getUmbral(), m.isActiva());
        }
    }

    public record MejoraRequest(
            @NotBlank @Size(max = 60) String nombre,
            @Size(max = 255) String descripcion,
            @NotNull @DecimalMin("0") BigDecimal precio,
            @Min(2) @Max(10) int multiplicadorBono,
            @NotNull CondicionMejora condicion,
            BigDecimal umbral,
            boolean activa) {
    }
}
