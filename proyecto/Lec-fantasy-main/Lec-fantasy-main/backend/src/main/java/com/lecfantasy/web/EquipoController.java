package com.lecfantasy.web;

import com.lecfantasy.dto.EquipoDtos.*;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.EquipoService;
import com.lecfantasy.service.MejoraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos/{equipoId}")
public class EquipoController {

    private final EquipoService equipos;
    private final MejoraService mejoras;

    public EquipoController(EquipoService equipos, MejoraService mejoras) {
        this.equipos = equipos;
        this.mejoras = mejoras;
    }

    @GetMapping
    public EquipoDto ver(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId) {
        return equipos.ver(yo, equipoId);
    }

    @PostMapping("/fichajes")
    public EquipoDto fichar(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId,
                            @Valid @RequestBody FichajeRequest req) {
        return equipos.fichar(yo, equipoId, req.jugadorId());
    }

    @DeleteMapping("/plantilla/{jugadorId}")
    public EquipoDto vender(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId, @PathVariable Long jugadorId) {
        return equipos.vender(yo, equipoId, jugadorId);
    }

    @PutMapping("/alineacion")
    public EquipoDto alinear(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId,
                             @Valid @RequestBody AlineacionRequest req) {
        return equipos.alinear(yo, equipoId, req);
    }

    @GetMapping("/transacciones")
    public List<TransaccionDto> transacciones(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId) {
        return equipos.transacciones(yo, equipoId);
    }

    @GetMapping("/puntuaciones")
    public List<PuntuacionDto> puntuaciones(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId) {
        return equipos.puntuaciones(yo, equipoId);
    }

    @GetMapping("/mejoras")
    public List<MejoraAplicadaDto> mejoras(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId) {
        return mejoras.delEquipo(yo, equipoId);
    }

    @PostMapping("/mejoras")
    @ResponseStatus(HttpStatus.CREATED)
    public MejoraAplicadaDto comprarMejora(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId,
                                           @Valid @RequestBody ComprarMejoraRequest req) {
        return mejoras.comprar(yo, equipoId, req);
    }

    @DeleteMapping("/mejoras/{mejoraAplicadaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarMejora(@AuthenticationPrincipal AuthUser yo, @PathVariable Long equipoId,
                               @PathVariable Long mejoraAplicadaId) {
        mejoras.cancelar(yo, equipoId, mejoraAplicadaId);
    }
}
