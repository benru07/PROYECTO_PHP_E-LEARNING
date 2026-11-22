package com.lecfantasy.web;

import com.lecfantasy.dto.ReporteDtos.CrearReporteRequest;
import com.lecfantasy.dto.ReporteDtos.ReporteDto;
import com.lecfantasy.dto.UsuarioDtos.*;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.ReporteService;
import com.lecfantasy.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService usuarios;
    private final ReporteService reportes;

    public UsuarioController(UsuarioService usuarios, ReporteService reportes) {
        this.usuarios = usuarios;
        this.reportes = reportes;
    }

    @GetMapping("/usuarios/me")
    public UsuarioPrivado yo(@AuthenticationPrincipal AuthUser yo) {
        return usuarios.yo(yo.id());
    }

    @PutMapping("/usuarios/me")
    public UsuarioPrivado actualizar(@AuthenticationPrincipal AuthUser yo, @Valid @RequestBody ActualizarPerfilRequest req) {
        return usuarios.actualizar(yo.id(), req);
    }

    @PutMapping("/usuarios/me/apariencia")
    public UsuarioPrivado apariencia(@AuthenticationPrincipal AuthUser yo, @Valid @RequestBody AparienciaRequest req) {
        return usuarios.cambiarApariencia(yo.id(), req);
    }

    @PutMapping("/usuarios/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(@AuthenticationPrincipal AuthUser yo, @Valid @RequestBody CambiarPasswordRequest req) {
        usuarios.cambiarPassword(yo.id(), req);
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioPublico perfil(@PathVariable Long id) {
        return usuarios.perfilPublico(id);
    }

    @PostMapping("/reportes")
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteDto reportar(@AuthenticationPrincipal AuthUser yo, @Valid @RequestBody CrearReporteRequest req) {
        return reportes.crear(yo, req);
    }
}
