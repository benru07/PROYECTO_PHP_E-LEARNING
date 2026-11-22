package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.EstadoReporte;
import com.lecfantasy.domain.Reporte;
import com.lecfantasy.domain.TipoActividad;
import com.lecfantasy.domain.Usuario;
import com.lecfantasy.dto.ReporteDtos.*;
import com.lecfantasy.repository.ReporteRepository;
import com.lecfantasy.repository.UsuarioRepository;
import com.lecfantasy.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reportes;
    private final UsuarioRepository usuarios;
    private final ActividadService actividad;

    public ReporteService(ReporteRepository reportes, UsuarioRepository usuarios, ActividadService actividad) {
        this.reportes = reportes;
        this.usuarios = usuarios;
        this.actividad = actividad;
    }

    @Transactional
    public ReporteDto crear(AuthUser yo, CrearReporteRequest req) {
        if (req.usuarioReportadoId().equals(yo.id())) {
            throw ApiException.peticionInvalida("No puedes reportarte a ti mismo");
        }
        Usuario reportado = usuarios.findById(req.usuarioReportadoId())
                .orElseThrow(() -> ApiException.noEncontrado("Usuario"));
        Reporte r = new Reporte();
        r.setReportador(usuarios.getReferenceById(yo.id()));
        r.setReportado(reportado);
        r.setMotivo(req.motivo());
        r.setDescripcion(AuthService.vacioANull(req.descripcion()));
        r.setFecha(LocalDateTime.now());
        reportes.save(r);
        return ReporteDto.of(reportes.findById(r.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<ReporteDto> listar(EstadoReporte estado) {
        return reportes.buscar(estado).stream().map(ReporteDto::of).toList();
    }

    @Transactional
    public ReporteDto resolver(AuthUser admin, Long id, ResolverReporteRequest req) {
        if (req.estado() == EstadoReporte.PENDIENTE) {
            throw ApiException.peticionInvalida("Elige RESUELTO o DESCARTADO");
        }
        Reporte r = reportes.findById(id).orElseThrow(() -> ApiException.noEncontrado("Reporte"));
        r.setEstado(req.estado());
        r.setResolucion(AuthService.vacioANull(req.resolucion()));
        r.setAdmin(usuarios.getReferenceById(admin.id()));
        r.setFechaResolucion(LocalDateTime.now());
        actividad.registrar(admin, TipoActividad.REPORTE, admin.nombre() + " marcó como " + req.estado()
                + " el reporte contra " + r.getReportado().getNombre());
        return ReporteDto.of(r);
    }
}
