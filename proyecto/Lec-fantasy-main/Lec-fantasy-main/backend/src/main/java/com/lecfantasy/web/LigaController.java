package com.lecfantasy.web;

import com.lecfantasy.dto.LigaDtos.*;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.LigaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ligas")
public class LigaController {

    private final LigaService ligas;

    public LigaController(LigaService ligas) {
        this.ligas = ligas;
    }

    @GetMapping("/publicas")
    public List<LigaDto> publicas(@AuthenticationPrincipal AuthUser yo) {
        return ligas.publicas(yo == null ? null : yo.id());
    }

    @GetMapping("/mias")
    public List<LigaDto> mias(@AuthenticationPrincipal AuthUser yo) {
        return ligas.mias(yo.id());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LigaDto crear(@AuthenticationPrincipal AuthUser yo, @Valid @RequestBody CrearLigaRequest req) {
        return ligas.crear(yo, req);
    }

    @PostMapping("/unirse")
    public LigaDto unirsePorCodigo(@AuthenticationPrincipal AuthUser yo, @Valid @RequestBody UnirsePorCodigoRequest req) {
        return ligas.unirsePorCodigo(yo, req);
    }

    @GetMapping("/{id}")
    public LigaDto detalle(@AuthenticationPrincipal AuthUser yo, @PathVariable Long id) {
        return ligas.detalle(yo, id);
    }

    @PostMapping("/{id}/unirse")
    public LigaDto unirse(@AuthenticationPrincipal AuthUser yo, @PathVariable Long id, @Valid @RequestBody UnirseRequest req) {
        return ligas.unirse(yo, id, req);
    }

    @GetMapping("/{id}/clasificacion")
    public List<ClasificacionFila> clasificacion(@AuthenticationPrincipal AuthUser yo, @PathVariable Long id,
                                                 @RequestParam(required = false) Long jornadaId) {
        return ligas.clasificacion(yo, id, jornadaId);
    }

    @DeleteMapping("/{id}/abandonar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void abandonar(@AuthenticationPrincipal AuthUser yo, @PathVariable Long id) {
        ligas.abandonar(yo, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@AuthenticationPrincipal AuthUser yo, @PathVariable Long id) {
        ligas.eliminar(yo, id);
    }
}
