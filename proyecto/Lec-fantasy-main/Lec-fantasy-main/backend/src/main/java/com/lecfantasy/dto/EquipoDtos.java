package com.lecfantasy.dto;

import com.lecfantasy.domain.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class EquipoDtos {

    private EquipoDtos() {
    }

    public record EquipoDto(Long id, String nombre, Long ligaId, String liga, Long usuarioId, String usuario,
                            BigDecimal dineroDisponible, BigDecimal puntosTotales, long posicion,
                            BigDecimal valorPlantilla, boolean mercadoAbierto, List<PlantillaDto> plantilla) {
    }

    public record PlantillaDto(Slot slot, EstadoPlantilla estado, CatalogoDtos.JugadorDto jugador,
                               BigDecimal precioCompra, LocalDateTime fechaFichaje) {
    }

    public record FichajeRequest(@NotNull Long jugadorId) {
    }

    /** Mapa slot → id de jugador. Los slots que no aparezcan quedan vacíos. */
    public record AlineacionRequest(@NotNull Map<Slot, Long> slots) {
    }

    public record TransaccionDto(Long id, TipoTransaccion tipo, Long jugadorId, String jugador, BigDecimal precio,
                                 LocalDateTime fecha) {

        public static TransaccionDto of(Transaccion t) {
            return new TransaccionDto(t.getId(), t.getTipo(), t.getJugador().getId(), t.getJugador().getNickname(),
                    t.getPrecio(), t.getFecha());
        }
    }

    public record PuntuacionDto(Long jornadaId, String jornada, BigDecimal puntosBase, BigDecimal puntosMejoras,
                                BigDecimal puntosTotales) {

        public static PuntuacionDto of(PuntuacionJornada p) {
            return new PuntuacionDto(p.getJornada().getId(), p.getJornada().getNombre(), p.getPuntosBase(),
                    p.getPuntosMejoras(), p.getPuntosTotales());
        }
    }

    public record ComprarMejoraRequest(@NotNull Long mejoraId, @NotNull Long jugadorId) {
    }

    public record MejoraAplicadaDto(Long id, CatalogoDtos.MejoraDto mejora, Long jugadorId, String jugador,
                                    Long jornadaId, String jornada, EstadoMejora estado, BigDecimal precioPagado,
                                    BigDecimal puntosExtra) {

        public static MejoraAplicadaDto of(MejoraAplicada m) {
            return new MejoraAplicadaDto(m.getId(), CatalogoDtos.MejoraDto.of(m.getMejora()), m.getJugador().getId(),
                    m.getJugador().getNickname(), m.getJornada().getId(), m.getJornada().getNombre(), m.getEstado(),
                    m.getPrecioPagado(), m.getPuntosExtra());
        }
    }
}
