package com.lecfantasy.dto;

import com.lecfantasy.domain.GrupoLiga;
import com.lecfantasy.domain.TipoLiga;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class LigaDtos {

    private LigaDtos() {
    }

    public record CrearLigaRequest(
            @NotBlank(message = "La liga necesita un nombre") @Size(max = 60) String nombre,
            @NotNull TipoLiga tipo,
            @NotNull @DecimalMin(value = "10000000", message = "Presupuesto mínimo 10.000.000")
            @DecimalMax(value = "500000000", message = "Presupuesto máximo 500.000.000") BigDecimal presupuestoInicial,
            @Min(value = 2, message = "Mínimo 2 participantes") @Max(value = 50, message = "Máximo 50 participantes")
            int maxParticipantes,
            @NotBlank(message = "Ponle nombre a tu equipo") @Size(max = 40) String nombreEquipo) {
    }

    public record UnirsePorCodigoRequest(
            @NotBlank(message = "Introduce el código") String codigo,
            @NotBlank(message = "Ponle nombre a tu equipo") @Size(max = 40) String nombreEquipo) {
    }

    public record UnirseRequest(@NotBlank(message = "Ponle nombre a tu equipo") @Size(max = 40) String nombreEquipo) {
    }

    /** codigoInvitacion solo se rellena para los miembros de la liga. */
    public record LigaDto(Long id, String nombre, TipoLiga tipo, boolean global, String codigoInvitacion,
                          BigDecimal presupuestoInicial, int maxParticipantes, long participantes,
                          String creador, Long creadorId, LocalDateTime fechaCreacion, Long miEquipoId) {

        public static LigaDto of(GrupoLiga g, long participantes, boolean esMiembro, Long miEquipoId) {
            return new LigaDto(g.getId(), g.getNombre(), g.getTipo(), g.isEsGlobal(),
                    esMiembro ? g.getCodigoInvitacion() : null, g.getPresupuestoInicial(), g.getMaxParticipantes(),
                    participantes, g.getCreador() == null ? null : g.getCreador().getNombre(),
                    g.getCreador() == null ? null : g.getCreador().getId(), g.getFechaCreacion(), miEquipoId);
        }
    }

    /**
     * puntosUltimaJornada: puntos en la última jornada cerrada (null si no ha puntuado nunca).
     * racha: últimas 5 jornadas, de la más antigua a la más reciente.
     */
    public record ClasificacionFila(int posicion, Long equipoId, String equipo, Long usuarioId, String usuario,
                                    BigDecimal puntos, BigDecimal puntosUltimaJornada, BigDecimal valorPlantilla,
                                    List<RachaDto> racha) {
    }

    /** Resultado de un equipo en una jornada: por encima o por debajo de la media de su liga. */
    public record RachaDto(Long jornadaId, String jornada, BigDecimal puntos, boolean porEncimaDeLaMedia) {
    }
}
