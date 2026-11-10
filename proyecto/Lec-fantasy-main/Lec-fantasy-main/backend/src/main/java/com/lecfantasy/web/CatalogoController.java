package com.lecfantasy.web;

import com.lecfantasy.common.PageResponse;
import com.lecfantasy.domain.Posicion;
import com.lecfantasy.dto.CatalogoDtos.*;
import com.lecfantasy.dto.JornadaDtos.*;
import com.lecfantasy.dto.NoticiaDtos.NoticiaDto;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.JornadaService;
import com.lecfantasy.service.JugadorService;
import com.lecfantasy.service.MejoraService;
import com.lecfantasy.service.NoticiaService;
import com.lecfantasy.service.calendario.CalendarioService;
import com.lecfantasy.service.calendario.PartidoCalendario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints de solo lectura, accesibles sin iniciar sesión. */
@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final JugadorService jugadores;
    private final JornadaService jornadas;
    private final MejoraService mejoras;
    private final NoticiaService noticias;
    private final CalendarioService calendario;

    public CatalogoController(JugadorService jugadores, JornadaService jornadas, MejoraService mejoras,
                              NoticiaService noticias, CalendarioService calendario) {
        this.calendario = calendario;
        this.jugadores = jugadores;
        this.jornadas = jornadas;
        this.mejoras = mejoras;
        this.noticias = noticias;
    }

    /**
     * Próximos partidos de la LEC (y los de las últimas 12 horas) según PandaScore.
     * Lista vacía si no hay PANDASCORE_TOKEN. La key se queda en el servidor.
     */
    @GetMapping("/calendario")
    public List<PartidoCalendario> calendario() {
        return calendario.proximos();
    }

    @GetMapping("/equipos-pro")
    public List<EquipoProDto> equiposPro() {
        return jugadores.equiposPro();
    }

    @GetMapping("/jugadores")
    public List<JugadorDto> jugadores(@RequestParam(required = false) Posicion posicion,
                                      @RequestParam(required = false) Long equipoId,
                                      @RequestParam(required = false) String q) {
        return jugadores.buscar(posicion, equipoId, q, false);
    }

    @GetMapping("/jugadores/{id}")
    public JugadorDetalleDto jugador(@PathVariable Long id) {
        return jugadores.detalle(id);
    }

    @GetMapping("/jornadas")
    public List<JornadaDto> jornadas() {
        return jornadas.listar();
    }

    @GetMapping("/jornadas/{id}")
    public JornadaDto jornada(@PathVariable Long id) {
        return jornadas.ver(id);
    }

    @GetMapping("/jornadas/{id}/partidas")
    public List<PartidaDto> partidas(@PathVariable Long id) {
        return jornadas.partidas(id);
    }

    @GetMapping("/partidas/{id}")
    public PartidaDetalleDto partida(@PathVariable Long id) {
        return jornadas.partida(id);
    }

    @GetMapping("/mejoras")
    public List<MejoraDto> mejoras() {
        return mejoras.catalogo(false);
    }

    @GetMapping("/noticias")
    public PageResponse<NoticiaDto> noticias(@RequestParam(defaultValue = "0") int pagina,
                                             @RequestParam(defaultValue = "10") int tamano) {
        return noticias.publicadas(pagina, tamano);
    }

    @GetMapping("/noticias/{id}")
    public NoticiaDto noticia(@PathVariable Long id, @AuthenticationPrincipal AuthUser yo) {
        return noticias.ver(id, yo != null && yo.esAdmin());
    }
}
