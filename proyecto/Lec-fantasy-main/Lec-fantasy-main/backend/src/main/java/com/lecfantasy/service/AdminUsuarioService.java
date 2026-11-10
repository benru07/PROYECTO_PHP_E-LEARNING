package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.common.PageResponse;
import com.lecfantasy.domain.*;
import com.lecfantasy.dto.AdminDtos.*;
import com.lecfantasy.repository.*;
import com.lecfantasy.security.AuthUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Panel de control y gestión de usuarios del administrador. */
@Service
public class AdminUsuarioService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UsuarioRepository usuarios;
    private final BaneoRepository baneos;
    private final ReporteRepository reportes;
    private final JugadorRealRepository jugadores;
    private final EquipoFantasyRepository equipos;
    private final RegistroActividadRepository actividades;
    private final LigaService ligaService;
    private final ActividadService actividad;
    private final MailService mail;

    public AdminUsuarioService(UsuarioRepository usuarios, BaneoRepository baneos, ReporteRepository reportes,
                               JugadorRealRepository jugadores, EquipoFantasyRepository equipos,
                               RegistroActividadRepository actividades, LigaService ligaService,
                               ActividadService actividad, MailService mail) {
        this.usuarios = usuarios;
        this.baneos = baneos;
        this.reportes = reportes;
        this.jugadores = jugadores;
        this.equipos = equipos;
        this.actividades = actividades;
        this.ligaService = ligaService;
        this.actividad = actividad;
        this.mail = mail;
    }

    @Transactional(readOnly = true)
    public DashboardDto dashboard() {
        LocalDateTime ahora = LocalDateTime.now();
        List<ActividadDto> recientes = actividades.findAllByOrderByFechaDesc(PageRequest.of(0, 10))
                .map(ActividadDto::of).getContent();
        return new DashboardDto(usuarios.count(), usuarios.countByUltimoAccesoAfter(LocalDate.now().atStartOfDay()),
                baneos.contarUsuariosBaneados(ahora), jugadores.countByBloqueadoTrue(),
                reportes.countByEstado(EstadoReporte.PENDIENTE), recientes);
    }

    @Transactional(readOnly = true)
    public PageResponse<ActividadDto> actividad(int pagina, int tamano) {
        return PageResponse.of(actividades.findAllByOrderByFechaDesc(
                PageRequest.of(Math.max(0, pagina), Math.min(Math.max(1, tamano), 100))), ActividadDto::of);
    }

    /** estado: null/"TODOS", "ACTIVO" o "BANEADO". */
    @Transactional(readOnly = true)
    public PageResponse<UsuarioAdminFila> buscar(String texto, String estado, String pais, int pagina, int tamano) {
        Boolean baneado = estado == null || estado.isBlank() || estado.equalsIgnoreCase("TODOS") ? null
                : estado.equalsIgnoreCase("BANEADO");
        String t = texto == null || texto.isBlank() ? null : texto.trim();
        String p = pais == null || pais.isBlank() || pais.equalsIgnoreCase("TODOS") ? null : pais.trim().toUpperCase(Locale.ROOT);
        LocalDateTime ahora = LocalDateTime.now();
        return PageResponse.of(usuarios.buscar(t, p, baneado, ahora,
                        PageRequest.of(Math.max(0, pagina), Math.min(Math.max(1, tamano), 100))),
                u -> fila(u, ahora));
    }

    @Transactional(readOnly = true)
    public UsuarioAdminDetalle detalle(Long id) {
        Usuario u = obtener(id);
        LocalDateTime ahora = LocalDateTime.now();
        GrupoLiga global = ligaService.ligaGlobalSiExiste().orElse(null);
        Optional<EquipoFantasy> equipoGlobal = global == null ? Optional.empty()
                : equipos.findByUsuarioIdAndGrupoId(id, global.getId());
        BigDecimal puntos = equipoGlobal.map(EquipoFantasy::getPuntosTotales).orElse(null);
        Long posicion = equipoGlobal.map(e -> equipos.posicionEnGrupo(global.getId(), e.getPuntosTotales())).orElse(null);
        Integer edad = u.getFechaNacimiento() == null ? null : Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears();
        List<BaneoDto> historial = baneos.historial(id).stream().map(BaneoDto::of).toList();
        return new UsuarioAdminDetalle(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), edad, u.getFechaNacimiento(),
                u.getPais(), u.getRiotId(), u.getEquipoFavorito() == null ? null : u.getEquipoFavorito().getNombre(),
                u.getFechaRegistro(), u.getUltimoAcceso(), puntos, posicion, reportes.countByReportadoId(id),
                !baneos.findActivos(id, ahora).isEmpty(), historial);
    }

    @Transactional
    public UsuarioAdminDetalle banear(AuthUser admin, Long id, BanearRequest req) {
        Usuario u = obtener(id);
        if (u.getId().equals(admin.id())) {
            throw ApiException.peticionInvalida("No puedes banearte a ti mismo");
        }
        if (u.getRol() == Rol.ADMIN) {
            throw ApiException.conflicto("Quita primero el rol de administrador");
        }
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin;
        switch (req.duracion()) {
            case H24:
                fin = inicio.plusHours(24);
                break;
            case D7:
                fin = inicio.plusDays(7);
                break;
            case D30:
                fin = inicio.plusDays(30);
                break;
            case PERSONALIZADO:
                if (req.hasta() == null || !req.hasta().isAfter(LocalDate.now())) {
                    throw ApiException.peticionInvalida("Indica una fecha futura para el baneo personalizado");
                }
                fin = req.hasta().plusDays(1).atStartOfDay();
                break;
            default:
                fin = null; // permanente
        }
        // Un baneo nuevo sustituye a los que estuvieran activos
        baneos.findActivos(id, inicio).forEach(b -> b.setRevocadoEn(inicio));
        Baneo b = new Baneo();
        b.setUsuario(u);
        b.setAdmin(usuarios.getReferenceById(admin.id()));
        b.setInicio(inicio);
        b.setFin(fin);
        b.setMotivo(req.motivo());
        b.setComentario(AuthService.vacioANull(req.comentario()));
        b.setNotificado(req.notificar());
        baneos.save(b);

        String duracion = fin == null ? "permanente" : "hasta el " + FECHA.format(fin.toLocalDate());
        if (req.notificar()) {
            mail.enviar(u.getEmail(), "LEC Fantasy · Tu cuenta ha sido suspendida",
                    "Hola " + u.getNombre() + ",\n\nTu cuenta ha sido suspendida (" + duracion + ") por el motivo: "
                            + req.motivo() + ".\n\nSi crees que es un error, responde a este correo.");
        }
        actividad.registrar(admin, TipoActividad.BANEO, admin.nombre() + " baneó a " + u.getNombre() + " (" + duracion + ")");
        return detalle(id);
    }

    @Transactional
    public UsuarioAdminDetalle desbanear(AuthUser admin, Long id) {
        Usuario u = obtener(id);
        LocalDateTime ahora = LocalDateTime.now();
        List<Baneo> activos = baneos.findActivos(id, ahora);
        if (activos.isEmpty()) {
            throw ApiException.conflicto("El usuario no está baneado");
        }
        activos.forEach(b -> b.setRevocadoEn(ahora));
        actividad.registrar(admin, TipoActividad.DESBANEO, admin.nombre() + " desbaneó a " + u.getNombre());
        return detalle(id);
    }

    @Transactional
    public UsuarioAdminDetalle cambiarRol(AuthUser admin, Long id, CambiarRolRequest req) {
        Usuario u = obtener(id);
        if (u.getId().equals(admin.id()) && req.rol() != Rol.ADMIN) {
            throw ApiException.peticionInvalida("No puedes quitarte el rol de administrador a ti mismo");
        }
        u.setRol(req.rol());
        actividad.registrar(admin, TipoActividad.CAMBIO_ROL, admin.nombre() + " cambió el rol de " + u.getNombre()
                + " a " + req.rol());
        return detalle(id);
    }

    /** "Eliminar de la BD": borra al usuario y, en cascada, sus equipos, fichajes, mejoras y reportes. */
    @Transactional
    public void eliminar(AuthUser admin, Long id) {
        Usuario u = obtener(id);
        if (u.getId().equals(admin.id())) {
            throw ApiException.peticionInvalida("No puedes eliminar tu propia cuenta desde aquí");
        }
        String nombre = u.getNombre();
        usuarios.delete(u);
        usuarios.flush();
        actividad.registrar(admin, TipoActividad.ELIMINAR_USUARIO, admin.nombre() + " eliminó al usuario " + nombre);
    }

    private UsuarioAdminFila fila(Usuario u, LocalDateTime ahora) {
        List<Baneo> activos = baneos.findActivos(u.getId(), ahora);
        boolean permanente = activos.stream().anyMatch(Baneo::isPermanente);
        LocalDateTime hasta = permanente ? null
                : activos.stream().map(Baneo::getFin).max(LocalDateTime::compareTo).orElse(null);
        return new UsuarioAdminFila(u.getId(), u.getNombre(), u.getEmail(), u.getPais(), u.getFechaRegistro(),
                u.getRol(), !activos.isEmpty(), hasta, permanente);
    }

    private Usuario obtener(Long id) {
        return usuarios.findById(id).orElseThrow(() -> ApiException.noEncontrado("Usuario"));
    }
}
