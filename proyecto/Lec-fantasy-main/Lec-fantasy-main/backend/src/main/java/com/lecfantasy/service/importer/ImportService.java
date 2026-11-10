package com.lecfantasy.service.importer;

import com.lecfantasy.config.AppProperties;
import com.lecfantasy.domain.*;
import com.lecfantasy.dto.JornadaDtos.ImportResultado;
import com.lecfantasy.repository.*;
import com.lecfantasy.service.Puntuacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Convierte líneas de estadísticas (de Leaguepedia o del CSV) en partidas, jugadores y equipos.
 * Es idempotente: una partida ya importada (mismo id externo) se omite.
 */
@Service
public class ImportService {

    private final PartidaRealRepository partidas;
    private final EstadisticaRepository estadisticas;
    private final JugadorRealRepository jugadores;
    private final EquipoProRepository equiposPro;
    private final AppProperties props;

    public ImportService(PartidaRealRepository partidas, EstadisticaRepository estadisticas,
                         JugadorRealRepository jugadores, EquipoProRepository equiposPro, AppProperties props) {
        this.partidas = partidas;
        this.estadisticas = estadisticas;
        this.jugadores = jugadores;
        this.equiposPro = equiposPro;
        this.props = props;
    }

    @Transactional
    public ImportResultado importar(Jornada jornada, List<StatLine> lineas, int ignoradasPrevias) {
        LocalDateTime desde = jornada.getFechaInicio().atStartOfDay();
        LocalDateTime hasta = jornada.getFechaFin().plusDays(1).atStartOfDay();
        Contexto ctx = new Contexto();
        ctx.ignoradas = ignoradasPrevias;

        Map<String, List<StatLine>> porPartida = new LinkedHashMap<>();
        for (StatLine l : lineas) {
            if (l.posicion() == null || l.equipo() == null || l.equipo().isBlank()) {
                ctx.ignoradas++;
                continue;
            }
            porPartida.computeIfAbsent(l.partidaId(), k -> new ArrayList<>()).add(l);
        }

        for (Map.Entry<String, List<StatLine>> entrada : porPartida.entrySet()) {
            String idExterno = entrada.getKey();
            List<StatLine> filas = entrada.getValue();
            LocalDateTime fecha = filas.get(0).fechaHora();
            if (fecha.isBefore(desde) || !fecha.isBefore(hasta)) {
                ctx.fueraDeFecha++;
                continue;
            }
            if (partidas.existsByIdExterno(idExterno)) {
                ctx.yaExistentes++;
                continue;
            }
            importarPartida(jornada, idExterno, filas, ctx);
            ctx.importadas++;
        }
        return new ImportResultado(ctx.importadas, ctx.yaExistentes, ctx.fueraDeFecha, ctx.jugadoresCreados,
                ctx.equiposCreados, ctx.ignoradas, ctx.avisos);
    }

    private void importarPartida(Jornada jornada, String idExterno, List<StatLine> filas, Contexto ctx) {
        if (filas.size() != 10) {
            ctx.avisos.add("La partida " + idExterno + " tiene " + filas.size() + " jugadores en lugar de 10");
        }
        List<String> nombresEquipo = filas.stream().map(StatLine::equipo).distinct().toList();
        if (nombresEquipo.size() != 2) {
            ctx.avisos.add("La partida " + idExterno + " tiene " + nombresEquipo.size() + " equipos");
        }
        String azul = filas.stream().filter(l -> Boolean.TRUE.equals(l.ladoAzul())).map(StatLine::equipo)
                .findFirst().orElse(nombresEquipo.get(0));
        String rojo = nombresEquipo.stream().filter(n -> !n.equals(azul)).findFirst().orElse(null);
        String ganador = filas.stream().filter(StatLine::victoria).map(StatLine::equipo).findFirst().orElse(null);

        PartidaReal p = new PartidaReal();
        p.setJornada(jornada);
        p.setIdExterno(idExterno);
        p.setFechaHora(filas.get(0).fechaHora());
        p.setEquipoAzul(equipo(azul, ctx));
        p.setEquipoRojo(rojo == null ? null : equipo(rojo, ctx));
        p.setGanador(ganador == null ? null : equipo(ganador, ctx));
        p.setDuracionSegundos(filas.stream().map(StatLine::duracionSegundos).filter(Objects::nonNull).findFirst().orElse(null));
        partidas.save(p);

        List<EstadisticaJugadorPartida> stats = new ArrayList<>();
        Set<Long> vistos = new HashSet<>();
        for (StatLine l : filas) {
            EquipoPro equipo = equipo(l.equipo(), ctx);
            JugadorReal jugador = jugador(l, equipo, ctx);
            if (!vistos.add(jugador.getId())) {
                ctx.avisos.add(jugador.getNickname() + " aparece dos veces en la partida " + idExterno);
                continue;
            }
            EstadisticaJugadorPartida e = new EstadisticaJugadorPartida();
            e.setPartida(p);
            e.setJugador(jugador);
            e.setEquipoPro(equipo);
            e.setCampeon(l.campeon());
            e.setKills(l.kills());
            e.setDeaths(l.deaths());
            e.setAssists(l.assists());
            e.setCs(l.cs());
            e.setVisionScore(l.visionScore());
            e.setDoubleKills(l.doubleKills());
            e.setTripleKills(l.tripleKills());
            e.setQuadraKills(l.quadraKills());
            e.setPentaKills(l.pentaKills());
            e.setFirstBlood(l.firstBlood());
            e.setVictoria(l.victoria());
            stats.add(e);
        }
        asignarMvpYPuntos(stats);
        estadisticas.saveAll(stats);
    }

    /** MVP = el jugador con más puntos base de la partida. */
    public static void asignarMvpYPuntos(List<EstadisticaJugadorPartida> stats) {
        EstadisticaJugadorPartida mvp = null;
        BigDecimal mejor = null;
        for (EstadisticaJugadorPartida e : stats) {
            e.setMvp(false);
            BigDecimal base = Puntuacion.base(e);
            if (mejor == null || base.compareTo(mejor) > 0) {
                mejor = base;
                mvp = e;
            }
        }
        if (mvp != null) {
            mvp.setMvp(true);
        }
        stats.forEach(e -> e.setPuntosFantasy(Puntuacion.total(e)));
    }

    private EquipoPro equipo(String nombre, Contexto ctx) {
        String clave = nombre.trim().toLowerCase(Locale.ROOT);
        return ctx.equipos.computeIfAbsent(clave, k -> equiposPro.findByNombreIgnoreCase(nombre.trim()).orElseGet(() -> {
            ctx.equiposCreados++;
            return equiposPro.save(new EquipoPro(nombre.trim()));
        }));
    }

    private JugadorReal jugador(StatLine l, EquipoPro equipo, Contexto ctx) {
        String clave = l.jugadorIdExterno() != null ? l.jugadorIdExterno() : "nick:" + l.jugador().toLowerCase(Locale.ROOT);
        JugadorReal cacheado = ctx.jugadores.get(clave);
        if (cacheado != null) {
            return cacheado;
        }
        Optional<JugadorReal> existente = l.jugadorIdExterno() == null ? Optional.empty()
                : jugadores.findByIdExterno(l.jugadorIdExterno());
        if (existente.isEmpty()) {
            existente = jugadores.findByNicknameIgnoreCase(l.jugador());
            existente.ifPresent(encontrado -> {
                if (encontrado.getIdExterno() == null && l.jugadorIdExterno() != null
                        && jugadores.findByIdExterno(l.jugadorIdExterno()).isEmpty()) {
                    encontrado.setIdExterno(l.jugadorIdExterno());
                }
            });
        }
        JugadorReal j = existente.orElseGet(() -> {
            JugadorReal nuevo = new JugadorReal();
            nuevo.setNickname(l.jugador().length() > 40 ? l.jugador().substring(0, 40) : l.jugador());
            nuevo.setPosicion(l.posicion());
            nuevo.setValorMercado(props.juego().valorInicialJugador());
            nuevo.setIdExterno(l.jugadorIdExterno());
            ctx.jugadoresCreados++;
            return jugadores.save(nuevo);
        });
        // Mantiene actualizado el equipo actual del jugador (traspasos entre splits)
        if (j.getEquipoPro() == null || !j.getEquipoPro().getId().equals(equipo.getId())) {
            j.setEquipoPro(equipo);
        }
        ctx.jugadores.put(clave, j);
        return j;
    }

    private static final class Contexto {
        int importadas;
        int yaExistentes;
        int fueraDeFecha;
        int jugadoresCreados;
        int equiposCreados;
        int ignoradas;
        final List<String> avisos = new ArrayList<>();
        final Map<String, EquipoPro> equipos = new HashMap<>();
        final Map<String, JugadorReal> jugadores = new HashMap<>();
    }
}
