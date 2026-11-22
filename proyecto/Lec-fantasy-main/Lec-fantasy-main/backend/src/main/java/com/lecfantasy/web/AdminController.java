package com.lecfantasy.web;

import com.lecfantasy.common.PageResponse;
import com.lecfantasy.domain.EstadoReporte;
import com.lecfantasy.domain.Posicion;
import com.lecfantasy.dto.AdminDtos.*;
import com.lecfantasy.dto.CatalogoDtos.*;
import com.lecfantasy.dto.JornadaDtos.*;
import com.lecfantasy.dto.NoticiaDtos.NoticiaDto;
import com.lecfantasy.dto.NoticiaDtos.NoticiaRequest;
import com.lecfantasy.dto.ReporteDtos.ReporteDto;
import com.lecfantasy.dto.ReporteDtos.ResolverReporteRequest;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Panel de administración. Todo /api/admin/** exige rol ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUsuarioService usuarios;
    private final JugadorService jugadores;
    private final JornadaService jornadas;
    private final MejoraService mejoras;
    private final NoticiaService noticias;
    private final ReporteService reportes;
    private final AutomatizacionJornadas automatizacion;

    public AdminController(AdminUsuarioService usuarios, JugadorService jugadores, JornadaService jornadas,
                           MejoraService mejoras, NoticiaService noticias, ReporteService reportes,
                           AutomatizacionJornadas automatizacion) {
        this.automatizacion = automatizacion;
        this.usuarios = usuarios;
        this.jugadores = jugadores;
        this.jornadas = jornadas;
        this.mejoras = mejoras;
        this.noticias = noticias;
        this.reportes = reportes;
    }

    // ---------- Panel de control ----------

    @GetMapping("/dashboard")
    public DashboardDto dashboard() {
        return usuarios.dashboard();
    }

    @GetMapping("/actividad")
    public PageResponse<ActividadDto> actividad(@RequestParam(defaultValue = "0") int pagina,
                                                @RequestParam(defaultValue = "30") int tamano) {
        return usuarios.actividad(pagina, tamano);
    }

    // ---------- Gestión de usuarios ----------

    @GetMapping("/usuarios")
    public PageResponse<UsuarioAdminFila> buscarUsuarios(@RequestParam(required = false) String q,
                                                         @RequestParam(required = false) String estado,
                                                         @RequestParam(required = false) String pais,
                                                         @RequestParam(defaultValue = "0") int pagina,
                                                         @RequestParam(defaultValue = "20") int tamano) {
        return usuarios.buscar(q, estado, pais, pagina, tamano);
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioAdminDetalle usuario(@PathVariable Long id) {
        return usuarios.detalle(id);
    }

    @PostMapping("/usuarios/{id}/baneo")
    public UsuarioAdminDetalle banear(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                      @Valid @RequestBody BanearRequest req) {
        return usuarios.banear(admin, id, req);
    }

    @DeleteMapping("/usuarios/{id}/baneo")
    public UsuarioAdminDetalle desbanear(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        return usuarios.desbanear(admin, id);
    }

    @PutMapping("/usuarios/{id}/rol")
    public UsuarioAdminDetalle cambiarRol(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                          @Valid @RequestBody CambiarRolRequest req) {
        return usuarios.cambiarRol(admin, id, req);
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarUsuario(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        usuarios.eliminar(admin, id);
    }

    // ---------- Jugadores, precios y bloqueos ----------

    @GetMapping("/jugadores")
    public List<JugadorDto> jugadores(@RequestParam(required = false) Posicion posicion,
                                      @RequestParam(required = false) Long equipoId,
                                      @RequestParam(required = false) String q) {
        return jugadores.buscar(posicion, equipoId, q, true);
    }

    @PostMapping("/jugadores")
    @ResponseStatus(HttpStatus.CREATED)
    public JugadorDto crearJugador(@AuthenticationPrincipal AuthUser admin, @Valid @RequestBody JugadorRequest req) {
        return jugadores.crear(admin, req);
    }

    @PutMapping("/jugadores/{id}")
    public JugadorDto editarJugador(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                    @Valid @RequestBody JugadorRequest req) {
        return jugadores.editar(admin, id, req);
    }

    @PostMapping("/jugadores/{id}/bloqueo")
    public JugadorDto bloquear(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                               @Valid @RequestBody BloqueoRequest req) {
        return jugadores.bloquear(admin, id, req.motivo());
    }

    @DeleteMapping("/jugadores/{id}/bloqueo")
    public JugadorDto desbloquear(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        return jugadores.desbloquear(admin, id);
    }

    @PostMapping("/equipos-pro")
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoProDto crearEquipoPro(@AuthenticationPrincipal AuthUser admin, @Valid @RequestBody EquipoProRequest req) {
        return jugadores.crearEquipoPro(admin, req);
    }

    @PutMapping("/equipos-pro/{id}")
    public EquipoProDto editarEquipoPro(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                        @Valid @RequestBody EquipoProRequest req) {
        return jugadores.editarEquipoPro(admin, id, req);
    }

    // ---------- Jornadas y puntuación ----------

    @PostMapping("/jornadas")
    @ResponseStatus(HttpStatus.CREATED)
    public JornadaDto crearJornada(@AuthenticationPrincipal AuthUser admin, @Valid @RequestBody JornadaRequest req) {
        return jornadas.crear(admin, req);
    }

    @PutMapping("/jornadas/{id}")
    public JornadaDto editarJornada(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                    @Valid @RequestBody JornadaRequest req) {
        return jornadas.editar(admin, id, req);
    }

    @DeleteMapping("/jornadas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarJornada(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        jornadas.eliminar(admin, id);
    }

    @PostMapping("/jornadas/{id}/iniciar")
    public JornadaDto iniciar(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        return jornadas.iniciar(admin, id);
    }

    @PostMapping("/jornadas/{id}/importar/leaguepedia")
    public ImportResultado importarLeaguepedia(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        return jornadas.importarLeaguepedia(admin, id);
    }

    @PostMapping(value = "/jornadas/{id}/importar/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResultado importarCsv(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                       @RequestParam("archivo") MultipartFile archivo,
                                       @RequestParam(defaultValue = "LEC") String liga) {
        return jornadas.importarCsv(admin, id, archivo, liga);
    }

    @GetMapping("/jornadas/{id}/previsualizacion")
    public PrevisualizacionDto previsualizar(@PathVariable Long id) {
        return jornadas.previsualizar(id);
    }

    /** Estado de la gestión automática de jornadas (Leaguepedia). */
    @GetMapping("/automatizacion")
    public AutomatizacionJornadas.Estado automatizacion() {
        return automatizacion.estado();
    }

    /** Ejecuta ahora la importación y el cierre/inicio automáticos de jornadas. */
    @PostMapping("/automatizacion/ejecutar")
    public AutomatizacionJornadas.Ejecucion ejecutarAutomatizacion(@AuthenticationPrincipal AuthUser admin) {
        return automatizacion.ejecutar(admin);
    }

    @PostMapping("/jornadas/{id}/finalizar")
    public FinalizacionResultado finalizar(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        return jornadas.finalizar(admin, id);
    }

    @DeleteMapping("/partidas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPartida(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        jornadas.eliminarPartida(admin, id);
    }

    // ---------- Mejoras ----------

    @GetMapping("/mejoras")
    public List<MejoraDto> mejoras() {
        return mejoras.catalogo(true);
    }

    @PostMapping("/mejoras")
    @ResponseStatus(HttpStatus.CREATED)
    public MejoraDto crearMejora(@AuthenticationPrincipal AuthUser admin, @Valid @RequestBody MejoraRequest req) {
        return mejoras.crear(admin, req);
    }

    @PutMapping("/mejoras/{id}")
    public MejoraDto editarMejora(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                  @Valid @RequestBody MejoraRequest req) {
        return mejoras.editar(admin, id, req);
    }

    // ---------- Noticias ----------

    @GetMapping("/noticias")
    public PageResponse<NoticiaDto> noticias(@RequestParam(defaultValue = "0") int pagina,
                                             @RequestParam(defaultValue = "20") int tamano) {
        return noticias.todas(pagina, tamano);
    }

    @PostMapping("/noticias")
    @ResponseStatus(HttpStatus.CREATED)
    public NoticiaDto crearNoticia(@AuthenticationPrincipal AuthUser admin, @Valid @RequestBody NoticiaRequest req) {
        return noticias.crear(admin, req);
    }

    @PutMapping("/noticias/{id}")
    public NoticiaDto editarNoticia(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                                    @Valid @RequestBody NoticiaRequest req) {
        return noticias.editar(admin, id, req);
    }

    @DeleteMapping("/noticias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarNoticia(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id) {
        noticias.eliminar(admin, id);
    }

    // ---------- Reportes y moderación ----------

    @GetMapping("/reportes")
    public List<ReporteDto> reportes(@RequestParam(required = false) EstadoReporte estado) {
        return reportes.listar(estado);
    }

    @PostMapping("/reportes/{id}/resolver")
    public ReporteDto resolver(@AuthenticationPrincipal AuthUser admin, @PathVariable Long id,
                               @Valid @RequestBody ResolverReporteRequest req) {
        return reportes.resolver(admin, id, req);
    }
}
