package com.lecfantasy.service;

import com.lecfantasy.config.AppProperties;
import com.lecfantasy.domain.EstadoJornada;
import com.lecfantasy.domain.TipoActividad;
import com.lecfantasy.dto.JornadaDtos.JornadaDto;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.calendario.CalendarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Lleva las jornadas solas, sin que el administrador tenga que pulsar nada:
 * <ol>
 *   <li><b>Calendario</b> (cada día, si hay token de PandaScore): crea las jornadas de las próximas
 *       semanas con partidos de la LEC.</li>
 *   <li><b>Inicio</b> (cada hora): el día de inicio de una jornada programada la pone EN_CURSO,
 *       lo que cierra el mercado y las alineaciones.</li>
 *   <li><b>Importación y cierre</b> (por defecto, los lunes a las 9:00): para cada jornada en curso
 *       que ya ha terminado, importa las partidas de Leaguepedia y la finaliza (puntos, mejoras y
 *       precios). Si Leaguepedia todavía no tiene las partidas, la deja en curso y lo vuelve a
 *       intentar en la siguiente ejecución.</li>
 * </ol>
 * Las horas se interpretan en la zona configurada (Europe/Madrid). Cada paso usa los mismos
 * métodos que los botones del panel de administración y queda en el registro de actividad como
 * "El sistema (automático)".
 */
@Service
public class AutomatizacionJornadas {

    private static final Logger log = LoggerFactory.getLogger(AutomatizacionJornadas.class);

    private final JornadaService jornadas;
    private final ActividadService actividad;
    private final CalendarioService calendario;
    private final AppProperties.Automatizacion config;
    private volatile Ejecucion ultima;

    public AutomatizacionJornadas(JornadaService jornadas, ActividadService actividad, CalendarioService calendario,
                                  AppProperties props) {
        this.calendario = calendario;
        this.jornadas = jornadas;
        this.actividad = actividad;
        this.config = props.automatizacion();
    }

    /** Resumen de una ejecución (lo muestra el panel de administración). */
    public record Ejecucion(LocalDateTime fecha, boolean manual, List<String> acciones) {
    }

    /** calendario: true si hay token de PandaScore para crear las jornadas solas. */
    public record Estado(boolean habilitada, String zona, String cronInicio, String cronImportacion,
                         String cronCalendario, boolean finalizar, boolean calendario, Ejecucion ultima) {
    }

    public Estado estado() {
        return new Estado(config.habilitada(), config.zona(), config.cronInicio(), config.cronImportacion(),
                config.cronCalendario(), config.finalizar(), calendario.disponible(), ultima);
    }

    // ------------------------- Tareas programadas -------------------------

    @Scheduled(cron = "${app.automatizacion.cron-inicio}", zone = "${app.automatizacion.zona}")
    public void tareaInicio() {
        if (config.habilitada()) {
            List<String> acciones = new ArrayList<>();
            iniciarPendientes(acciones);
            if (!acciones.isEmpty()) {
                guardar(null, acciones);
            }
        }
    }

    @Scheduled(cron = "${app.automatizacion.cron-calendario}", zone = "${app.automatizacion.zona}")
    public void tareaCalendario() {
        if (config.habilitada() && calendario.disponible()) {
            List<String> acciones = new ArrayList<>();
            crearDesdeCalendario(acciones);
            if (!acciones.isEmpty()) {
                guardar(null, acciones);
            }
        }
    }

    @Scheduled(cron = "${app.automatizacion.cron-importacion}", zone = "${app.automatizacion.zona}")
    public void tareaImportacion() {
        if (config.habilitada()) {
            ejecutar(null);
        }
    }

    /**
     * Hace las dos cosas ahora mismo. Con admin = botón "Ejecutar ahora" del panel;
     * con null = la tarea programada.
     */
    public Ejecucion ejecutar(AuthUser admin) {
        List<String> acciones = new ArrayList<>();
        crearDesdeCalendario(acciones);
        importarYCerrarTerminadas(acciones);
        iniciarPendientes(acciones); // tras cerrar una jornada puede tocar empezar la siguiente
        if (acciones.isEmpty()) {
            acciones.add("Nada que hacer: no hay jornadas nuevas en el calendario, ni terminadas, ni que empiecen hoy.");
        }
        return guardar(admin, acciones);
    }

    // ------------------------- Pasos -------------------------

    private void crearDesdeCalendario(List<String> acciones) {
        if (!calendario.disponible()) {
            return;
        }
        try {
            acciones.addAll(calendario.crearJornadas());
        } catch (RuntimeException e) {
            log.warn("No se pudo leer el calendario de PandaScore", e);
            acciones.add("Calendario de PandaScore: error (" + e.getMessage() + ").");
        }
    }

    private void importarYCerrarTerminadas(List<String> acciones) {
        LocalDate hoy = hoy();
        for (JornadaDto j : jornadas.listar()) {
            if (j.estado() != EstadoJornada.EN_CURSO || !j.fechaFin().isBefore(hoy)) {
                continue;
            }
            try {
                var r = jornadas.importarLeaguepedia(null, j.id());
                acciones.add("«" + j.nombre() + "»: " + r.partidasImportadas() + " partidas nuevas de Leaguepedia ("
                        + r.partidasYaExistentes() + " ya estaban).");
                if (jornadas.numeroPartidas(j.id()) == 0) {
                    acciones.add("«" + j.nombre() + "»: Leaguepedia aún no tiene partidas; se reintentará en la próxima ejecución.");
                    continue;
                }
                if (config.finalizar()) {
                    var fin = jornadas.finalizar(null, j.id());
                    acciones.add("«" + j.nombre() + "» finalizada: " + fin.equiposPuntuados() + " equipos puntuados, "
                            + fin.preciosActualizados() + " precios actualizados.");
                } else {
                    acciones.add("«" + j.nombre() + "» importada; pendiente de que un administrador la finalice.");
                }
            } catch (RuntimeException e) {
                log.warn("No se pudo procesar la jornada {} automáticamente", j.id(), e);
                acciones.add("«" + j.nombre() + "»: error al importar (" + e.getMessage() + "). Se reintentará.");
            }
        }
    }

    private void iniciarPendientes(List<String> acciones) {
        LocalDate hoy = hoy();
        List<JornadaDto> todas = jornadas.listar();
        if (todas.stream().anyMatch(j -> j.estado() == EstadoJornada.EN_CURSO)) {
            return; // solo puede haber una jornada en curso
        }
        todas.stream()
                .filter(j -> j.estado() == EstadoJornada.PROGRAMADA && !j.fechaInicio().isAfter(hoy))
                .findFirst()
                .ifPresent(j -> {
                    try {
                        jornadas.iniciar(null, j.id());
                        acciones.add("«" + j.nombre() + "» iniciada: mercado y alineaciones cerrados.");
                    } catch (RuntimeException e) {
                        log.warn("No se pudo iniciar la jornada {} automáticamente", j.id(), e);
                        acciones.add("«" + j.nombre() + "»: no se pudo iniciar (" + e.getMessage() + ").");
                    }
                });
    }

    private Ejecucion guardar(AuthUser admin, List<String> acciones) {
        ultima = new Ejecucion(LocalDateTime.now(ZoneOffset.UTC), admin != null, List.copyOf(acciones));
        acciones.forEach(a -> log.info("Automatización de jornadas: {}", a));
        actividad.registrar(admin, TipoActividad.JORNADA, (admin == null ? "Automatización: " : admin.nombre()
                + " ejecutó la automatización: ") + String.join(" ", acciones));
        return ultima;
    }

    private LocalDate hoy() {
        return LocalDate.now(ZoneId.of(config.zona()));
    }
}
