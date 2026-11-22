package com.lecfantasy.dto;

import com.lecfantasy.domain.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record DashboardDto(long usuarios, long activosHoy, long baneados, long jugadoresBloqueados,
                               long reportesPendientes, List<ActividadDto> actividadReciente) {
    }

    public record ActividadDto(Long id, String admin, TipoActividad tipo, String descripcion, LocalDateTime fecha) {

        public static ActividadDto of(RegistroActividad a) {
            return new ActividadDto(a.getId(), a.getAdminNombre(), a.getTipo(), a.getDescripcion(), a.getFecha());
        }
    }

    /** Fila de la tabla "Gestión de usuarios". */
    public record UsuarioAdminFila(Long id, String nombre, String email, String pais, LocalDateTime fechaRegistro,
                                   Rol rol, boolean baneado, LocalDateTime baneadoHasta, boolean banPermanente) {
    }

    /** Panel "Perfil seleccionado". */
    public record UsuarioAdminDetalle(Long id, String nombre, String email, Rol rol, Integer edad,
                                      LocalDate fechaNacimiento, String pais, String riotId, String equipoFavorito,
                                      LocalDateTime fechaRegistro, LocalDateTime ultimoAcceso,
                                      BigDecimal puntosLigaGlobal, Long posicionLigaGlobal, long reportesRecibidos,
                                      boolean baneado, List<BaneoDto> historialBaneos) {
    }

    public record BaneoDto(Long id, LocalDateTime inicio, LocalDateTime fin, boolean permanente, MotivoBaneo motivo,
                           String comentario, String admin, LocalDateTime revocadoEn) {

        public static BaneoDto of(Baneo b) {
            return new BaneoDto(b.getId(), b.getInicio(), b.getFin(), b.isPermanente(), b.getMotivo(),
                    b.getComentario(), b.getAdmin() == null ? null : b.getAdmin().getNombre(), b.getRevocadoEn());
        }
    }

    public enum DuracionBaneo {
        H24, D7, D30, PERSONALIZADO, PERMANENTE
    }

    public record BanearRequest(@NotNull DuracionBaneo duracion, LocalDate hasta, @NotNull MotivoBaneo motivo,
                                @Size(max = 500) String comentario, boolean notificar) {
    }

    public record CambiarRolRequest(@NotNull Rol rol) {
    }
}
