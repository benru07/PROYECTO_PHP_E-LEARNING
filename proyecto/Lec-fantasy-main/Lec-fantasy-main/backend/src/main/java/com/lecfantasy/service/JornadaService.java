package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.*;
import com.lecfantasy.dto.CatalogoDtos.EstadisticaDto;
import com.lecfantasy.dto.JornadaDtos.*;
import com.lecfantasy.repository.*;
import com.lecfantasy.security.AuthUser;
import com.lecfantasy.service.importer.ImportService;
import com.lecfantasy.service.importer.LeaguepediaClient;
import com.lecfantasy.service.importer.OraclesElixirParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Ciclo de vida de una jornada:
 * PROGRAMADA → (iniciar) EN_CURSO [mercado cerrado, se importan partidas] → (finalizar) FINALIZADA.
 */
@Service
public class JornadaService {

    /** Media de puntos por partida a partir de la cual un jugador sube de precio. */
    static final BigDecimal MEDIA_REFERENCIA = new BigDecimal("25");
    static final BigDecimal VARIACION_MAXIMA = new BigDecimal("0.08");
    static final BigDecimal VALOR_MINIMO = new BigDecimal("500000");
    static final BigDecimal REDONDEO_PRECIO = new BigDecimal("10000");

    private final JornadaRepository jornadas;
    private final PartidaRealRepository partidas;
    private final EstadisticaRepository estadisticas;
    private final EquipoFantasyRepository equipos;
    private final PlantillaJugadorRepository plantillas;
    private final MejoraAplicadaRepository mejorasAplicadas;
    private final PuntuacionJornadaRepository puntuaciones;
    private final ImportService importService;
    private final LeaguepediaClient leaguepedia;
    private final OraclesElixirParser oraclesElixir;
    private final ActividadService actividad;
    private final HistorialValorRepository historialValor;

    public JornadaService(JornadaRepository jornadas, PartidaRealRepository partidas,
                          EstadisticaRepository estadisticas, EquipoFantasyRepository equipos,
                          PlantillaJugadorRepository plantillas, MejoraAplicadaRepository mejorasAplicadas,
                          PuntuacionJornadaRepository puntuaciones, ImportService importService,
                          LeaguepediaClient leaguepedia, OraclesElixirParser oraclesElixir,
                          ActividadService actividad, HistorialValorRepository historialValor) {
        this.historialValor = historialValor;
        this.jornadas = jornadas;
        this.partidas = partidas;
        this.estadisticas = estadisticas;
        this.equipos = equipos;
        this.plantillas = plantillas;
        this.mejorasAplicadas = mejorasAplicadas;
        this.puntuaciones = puntuaciones;
        this.importService = importService;
        this.leaguepedia = leaguepedia;
        this.oraclesElixir = oraclesElixir;
        this.actividad = actividad;
    }

    // ------------------------- Consultas públicas -------------------------

    @Transactional(readOnly = true)
    public List<JornadaDto> listar() {
        return jornadas.findAllByOrderByFechaInicioAsc().stream().map(JornadaDto::of).toList();
    }

    @Transactional(readOnly = true)
    public JornadaDto ver(Long id) {
        return JornadaDto.of(obtener(id));
    }

    @Transactional(readOnly = true)
    public List<PartidaDto> partidas(Long jornadaId) {
        obtener(jornadaId);
        return partidas.findByJornada(jornadaId).stream().map(PartidaDto::of).toList();
    }

    @Transactional(readOnly = true)
    public PartidaDetalleDto partida(Long partidaId) {
        PartidaReal p = partidas.findById(partidaId).orElseThrow(() -> ApiException.noEncontrado("Partida"));
        List<EstadisticaDto> stats = estadisticas.findByPartida(partidaId).stream()
                .sorted(Comparator.comparing((EstadisticaJugadorPartida e) -> e.getEquipoPro() == null ? 0L : e.getEquipoPro().getId())
                        .thenComparing(e -> e.getJugador().getPosicion().ordinal()))
                .map(EstadisticaDto::of).toList();
        return new PartidaDetalleDto(PartidaDto.of(p), stats);
    }

    // ------------------------- Administración -------------------------

    @Transactional
    public JornadaDto crear(AuthUser admin, JornadaRequest req) {
        validarFechas(req);
        Jornada j = new Jornada();
        aplicar(j, req);
        jornadas.save(j);
        actividad.registrar(admin, TipoActividad.JORNADA, quien(admin) + " creó la jornada «" + j.getNombre() + "»");
        return JornadaDto.of(j);
    }

    @Transactional
    public JornadaDto editar(AuthUser admin, Long id, JornadaRequest req) {
        Jornada j = obtener(id);
        if (j.getEstado() == EstadoJornada.FINALIZADA) {
            throw ApiException.conflicto("Una jornada finalizada no se puede editar");
        }
        validarFechas(req);
        aplicar(j, req);
        return JornadaDto.of(j);
    }

    /** Solo jornadas programadas. Devuelve el dinero de las mejoras compradas para ella. */
    @Transactional
    public void eliminar(AuthUser admin, Long id) {
        Jornada j = obtener(id);
        if (j.getEstado() != EstadoJornada.PROGRAMADA) {
            throw ApiException.conflicto("Solo se pueden eliminar jornadas programadas");
        }
        for (MejoraAplicada m : mejorasAplicadas.findByJornadaYEstado(id, EstadoMejora.PENDIENTE)) {
            EquipoFantasy e = m.getEquipo();
            e.setDineroDisponible(e.getDineroDisponible().add(m.getPrecioPagado()));
        }
        jornadas.delete(j);
        actividad.registrar(admin, TipoActividad.JORNADA, admin.nombre() + " eliminó la jornada «" + j.getNombre() + "»");
    }

    @Transactional
    public JornadaDto iniciar(AuthUser admin, Long id) {
        Jornada j = obtener(id);
        if (j.getEstado() != EstadoJornada.PROGRAMADA) {
            throw ApiException.conflicto("Solo se puede iniciar una jornada programada");
        }
        if (jornadas.existsByEstado(EstadoJornada.EN_CURSO)) {
            throw ApiException.conflicto("Ya hay otra jornada en curso");
        }
        j.setEstado(EstadoJornada.EN_CURSO);
        actividad.registrar(admin, TipoActividad.JORNADA, quien(admin) + " inició la jornada «" + j.getNombre()
                + "» (mercado cerrado)");
        return JornadaDto.of(j);
    }

    @Transactional
    public ImportResultado importarLeaguepedia(AuthUser admin, Long id) {
        Jornada j = jornadaImportable(id);
        var lineas = leaguepedia.obtener(j.getFuenteOverview(), j.getFechaInicio(), j.getFechaFin());
        ImportResultado r = importService.importar(j, lineas, 0);
        registrarImportacion(admin, j, "Leaguepedia", r);
        return r;
    }

    @Transactional
    public ImportResultado importarCsv(AuthUser admin, Long id, MultipartFile archivo, String liga) {
        Jornada j = jornadaImportable(id);
        if (archivo == null || archivo.isEmpty()) {
            throw ApiException.peticionInvalida("Adjunta el CSV de Oracle's Elixir");
        }
        OraclesElixirParser.Resultado leido;
        try (InputStream in = archivo.getInputStream()) {
            leido = oraclesElixir.leer(in, liga == null || liga.isBlank() ? "LEC" : liga.trim());
        } catch (IOException e) {
            throw ApiException.peticionInvalida("No se pudo leer el archivo");
        }
        ImportResultado r = importService.importar(j, leido.lineas(), leido.ignoradas());
        registrarImportacion(admin, j, "CSV de Oracle's Elixir", r);
        return r;
    }

    @Transactional
    public void eliminarPartida(AuthUser admin, Long partidaId) {
        PartidaReal p = partidas.findById(partidaId).orElseThrow(() -> ApiException.noEncontrado("Partida"));
        if (p.getJornada().getEstado() == EstadoJornada.FINALIZADA) {
            throw ApiException.conflicto("No se pueden borrar partidas de una jornada finalizada");
        }
        partidas.delete(p);
        actividad.registrar(admin, TipoActividad.JORNADA, admin.nombre() + " eliminó la partida " + p.getIdExterno());
    }

    /** "Validar puntos": puntos de cada jugador en la jornada antes de cerrarla. */
    @Transactional
    public PrevisualizacionDto previsualizar(Long id) {
        Jornada j = obtener(id);
        List<EstadisticaJugadorPartida> stats = recalcularPuntos(id);
        Map<JugadorReal, List<EstadisticaJugadorPartida>> porJugador = stats.stream()
                .collect(Collectors.groupingBy(EstadisticaJugadorPartida::getJugador));
        List<PuntosJugadorDto> filas = porJugador.entrySet().stream()
                .map(en -> new PuntosJugadorDto(en.getKey().getId(), en.getKey().getNickname(), en.getKey().getPosicion(),
                        en.getKey().getEquipoPro() == null ? null : en.getKey().getEquipoPro().getNombre(),
                        en.getValue().size(), suma(en.getValue())))
                .sorted(Comparator.comparing(PuntosJugadorDto::puntos).reversed())
                .toList();
        int numPartidas = (int) stats.stream().map(e -> e.getPartida().getId()).distinct().count();
        return new PrevisualizacionDto(JornadaDto.of(j), numPartidas, filas);
    }

    /**
     * Cierra la jornada: puntúa a todos los equipos fantasy (solo titulares), resuelve
     * las mejoras y actualiza el valor de mercado de los jugadores según su rendimiento.
     */
    @Transactional
    public FinalizacionResultado finalizar(AuthUser admin, Long id) {
        Jornada j = obtener(id);
        if (j.getEstado() != EstadoJornada.EN_CURSO) {
            throw ApiException.conflicto("Solo se puede finalizar una jornada en curso");
        }
        List<EstadisticaJugadorPartida> stats = recalcularPuntos(id);
        Map<Long, List<EstadisticaJugadorPartida>> statsPorJugador = stats.stream()
                .collect(Collectors.groupingBy(e -> e.getJugador().getId()));
        Map<Long, BigDecimal> puntosPorJugador = new HashMap<>();
        statsPorJugador.forEach((jugadorId, lista) -> puntosPorJugador.put(jugadorId, suma(lista)));

        // Titulares de cada equipo en el momento del cierre
        Map<Long, List<PlantillaJugador>> plantillaPorEquipo = plantillas.findAllConEquipoYJugador().stream()
                .collect(Collectors.groupingBy(p -> p.getEquipo().getId()));

        // Mejoras pendientes de esta jornada, agrupadas por equipo
        Map<Long, List<MejoraAplicada>> mejorasPorEquipo = mejorasAplicadas
                .findByJornadaYEstado(id, EstadoMejora.PENDIENTE).stream()
                .collect(Collectors.groupingBy(m -> m.getEquipo().getId()));

        int cumplidas = 0;
        int fallidas = 0;
        List<EquipoFantasy> todos = equipos.findAll();
        for (EquipoFantasy equipo : todos) {
            List<PlantillaJugador> plantilla = plantillaPorEquipo.getOrDefault(equipo.getId(), List.of());
            Set<Long> titulares = plantilla.stream().filter(p -> p.getSlot().esTitular())
                    .map(p -> p.getJugador().getId()).collect(Collectors.toSet());
            BigDecimal base = titulares.stream().map(jid -> puntosPorJugador.getOrDefault(jid, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal extra = BigDecimal.ZERO;
            for (MejoraAplicada m : mejorasPorEquipo.getOrDefault(equipo.getId(), List.of())) {
                Long jugadorId = m.getJugador().getId();
                boolean cumple = titulares.contains(jugadorId)
                        && EvaluadorMejoras.cumple(m.getMejora(), statsPorJugador.getOrDefault(jugadorId, List.of()));
                if (cumple) {
                    BigDecimal bonus = puntosPorJugador.getOrDefault(jugadorId, BigDecimal.ZERO)
                            .multiply(BigDecimal.valueOf(m.getMejora().getMultiplicadorBono() - 1L))
                            .setScale(2, RoundingMode.HALF_UP);
                    m.setEstado(EstadoMejora.CUMPLIDA);
                    m.setPuntosExtra(bonus);
                    extra = extra.add(bonus);
                    cumplidas++;
                } else {
                    m.setEstado(EstadoMejora.FALLIDA);
                    m.setPuntosExtra(BigDecimal.ZERO);
                    fallidas++;
                }
            }

            PuntuacionJornada pj = new PuntuacionJornada();
            pj.setEquipo(equipo);
            pj.setJornada(j);
            pj.setPuntosBase(base.setScale(2, RoundingMode.HALF_UP));
            pj.setPuntosMejoras(extra.setScale(2, RoundingMode.HALF_UP));
            pj.setPuntosTotales(base.add(extra).setScale(2, RoundingMode.HALF_UP));
            puntuaciones.save(pj);
            equipo.setPuntosTotales(equipo.getPuntosTotales().add(pj.getPuntosTotales()));
        }

        int precios = actualizarPrecios(j, statsPorJugador, puntosPorJugador);

        j.setEstado(EstadoJornada.FINALIZADA);
        j.setFechaFinalizacion(LocalDateTime.now());
        actividad.registrar(admin, TipoActividad.JORNADA, quien(admin) + " finalizó la jornada «" + j.getNombre()
                + "»: " + todos.size() + " equipos puntuados");
        return new FinalizacionResultado(JornadaDto.of(j), todos.size(), cumplidas, fallidas, precios);
    }

    // ------------------------- Auxiliares -------------------------

    /** Recalcula MVP y puntos de todas las partidas (por si se cambiaron las reglas). */
    private List<EstadisticaJugadorPartida> recalcularPuntos(Long jornadaId) {
        List<EstadisticaJugadorPartida> stats = estadisticas.findByJornada(jornadaId);
        stats.stream().collect(Collectors.groupingBy(e -> e.getPartida().getId()))
                .values().forEach(ImportService::asignarMvpYPuntos);
        return stats;
    }

    /**
     * Cada jugador que ha jugado sube o baja hasta un 8 % según su media de puntos
     * respecto a la referencia (25 puntos por partida).
     */
    private int actualizarPrecios(Jornada jornada, Map<Long, List<EstadisticaJugadorPartida>> statsPorJugador,
                                  Map<Long, BigDecimal> puntosPorJugador) {
        int cambiados = 0;
        LocalDateTime ahora = LocalDateTime.now();
        for (Map.Entry<Long, List<EstadisticaJugadorPartida>> en : statsPorJugador.entrySet()) {
            JugadorReal jugador = en.getValue().get(0).getJugador();
            BigDecimal media = puntosPorJugador.get(en.getKey())
                    .divide(BigDecimal.valueOf(en.getValue().size()), 4, RoundingMode.HALF_UP);
            BigDecimal variacion = media.subtract(MEDIA_REFERENCIA)
                    .divide(new BigDecimal("250"), 4, RoundingMode.HALF_UP)
                    .max(VARIACION_MAXIMA.negate()).min(VARIACION_MAXIMA);
            BigDecimal nuevo = jugador.getValorMercado().multiply(BigDecimal.ONE.add(variacion))
                    .divide(REDONDEO_PRECIO, 0, RoundingMode.HALF_UP).multiply(REDONDEO_PRECIO)
                    .max(VALOR_MINIMO).setScale(2, RoundingMode.HALF_UP);
            if (jugador.cambiarValor(nuevo)) {
                // Se guarda para la gráfica de evolución del precio y la variación del mercado
                historialValor.registrarCambio(jugador, jornada, ahora);
                cambiados++;
            }
        }
        return cambiados;
    }

    private static BigDecimal suma(List<EstadisticaJugadorPartida> lista) {
        return lista.stream().map(EstadisticaJugadorPartida::getPuntosFantasy).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Jornada jornadaImportable(Long id) {
        Jornada j = obtener(id);
        if (j.getEstado() == EstadoJornada.FINALIZADA) {
            throw ApiException.conflicto("La jornada ya está finalizada");
        }
        return j;
    }

    /** Nombre para el registro de actividad: el admin o, si actúa la tarea programada, "El sistema". */
    private static String quien(AuthUser admin) {
        return admin == null ? "El sistema (automático)" : admin.nombre();
    }

    /** Partidas importadas en una jornada (la tarea automática solo cierra jornadas con datos). */
    @Transactional(readOnly = true)
    public int numeroPartidas(Long jornadaId) {
        return partidas.findByJornada(jornadaId).size();
    }

    private void registrarImportacion(AuthUser admin, Jornada j, String fuente, ImportResultado r) {
        actividad.registrar(admin, TipoActividad.IMPORTACION, quien(admin) + " importó " + r.partidasImportadas()
                + " partidas desde " + fuente + " en «" + j.getNombre() + "»");
    }

    private static void validarFechas(JornadaRequest req) {
        if (req.fechaFin().isBefore(req.fechaInicio())) {
            throw ApiException.peticionInvalida("La fecha de fin no puede ser anterior a la de inicio");
        }
    }

    private static void aplicar(Jornada j, JornadaRequest req) {
        j.setNombre(req.nombre().trim());
        j.setFechaInicio(req.fechaInicio());
        j.setFechaFin(req.fechaFin());
        j.setFuenteOverview(req.fuenteOverview() == null || req.fuenteOverview().isBlank() ? null : req.fuenteOverview().trim());
    }

    private Jornada obtener(Long id) {
        return jornadas.findById(id).orElseThrow(() -> ApiException.noEncontrado("Jornada"));
    }
}
