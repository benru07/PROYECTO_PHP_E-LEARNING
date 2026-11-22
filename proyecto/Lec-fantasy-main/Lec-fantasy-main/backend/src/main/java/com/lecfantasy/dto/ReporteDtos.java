package com.lecfantasy.dto;

import com.lecfantasy.domain.EstadoReporte;
import com.lecfantasy.domain.MotivoReporte;
import com.lecfantasy.domain.Reporte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class ReporteDtos {

    private ReporteDtos() {
    }

    public record CrearReporteRequest(@NotNull Long usuarioReportadoId, @NotNull MotivoReporte motivo,
                                      @Size(max = 1000) String descripcion) {
    }

    public record ResolverReporteRequest(@NotNull EstadoReporte estado, @Size(max = 500) String resolucion) {
    }

    public record ReporteDto(Long id, Long reportadorId, String reportador, Long reportadoId, String reportado,
                             MotivoReporte motivo, String descripcion, EstadoReporte estado, LocalDateTime fecha,
                             String admin, String resolucion, LocalDateTime fechaResolucion) {

        public static ReporteDto of(Reporte r) {
            return new ReporteDto(r.getId(), r.getReportador().getId(), r.getReportador().getNombre(),
                    r.getReportado().getId(), r.getReportado().getNombre(), r.getMotivo(), r.getDescripcion(),
                    r.getEstado(), r.getFecha(), r.getAdmin() == null ? null : r.getAdmin().getNombre(),
                    r.getResolucion(), r.getFechaResolucion());
        }
    }
}
