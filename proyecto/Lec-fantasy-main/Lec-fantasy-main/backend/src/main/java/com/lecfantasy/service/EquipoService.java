package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.*;
import com.lecfantasy.dto.EquipoDtos.*;
import com.lecfantasy.repository.*;
import com.lecfantasy.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/** Equipo fantasy: mercado (fichar / vender) y alineación. */
@Service
public class EquipoService {

    private static final List<Slot> ORDEN_SLOTS = List.of(Slot.values());

    private final EquipoFantasyRepository equipos;
    private final PlantillaJugadorRepository plantillas;
    private final JugadorRealRepository jugadores;
    private final TransaccionRepository transacciones;
    private final PuntuacionJornadaRepository puntuaciones;
    private final JornadaRepository jornadas;
    private final JugadorService jugadorService;

    public EquipoService(EquipoFantasyRepository equipos, PlantillaJugadorRepository plantillas,
                         JugadorRealRepository jugadores, TransaccionRepository transacciones,
                         PuntuacionJornadaRepository puntuaciones, JornadaRepository jornadas,
                         JugadorService jugadorService) {
        this.equipos = equipos;
        this.plantillas = plantillas;
        this.jugadores = jugadores;
        this.transacciones = transacciones;
        this.puntuaciones = puntuaciones;
        this.jornadas = jornadas;
        this.jugadorService = jugadorService;
    }

    // ------------------------- Consultas -------------------------

    @Transactional(readOnly = true)
    public EquipoDto ver(AuthUser yo, Long equipoId) {
        return dto(visible(yo, equipoId));
    }

    @Transactional(readOnly = true)
    public List<TransaccionDto> transacciones(AuthUser yo, Long equipoId) {
        visible(yo, equipoId);
        return transacciones.findByEquipo(equipoId).stream().map(TransaccionDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<PuntuacionDto> puntuaciones(AuthUser yo, Long equipoId) {
        visible(yo, equipoId);
        return puntuaciones.findByEquipo(equipoId).stream().map(PuntuacionDto::of).toList();
    }

    // ------------------------- Mercado -------------------------

    @Transactional
    public EquipoDto fichar(AuthUser yo, Long equipoId, Long jugadorId) {
        EquipoFantasy equipo = propio(yo, equipoId);
        comprobarMercadoAbierto();
        JugadorReal jugador = jugadores.findById(jugadorId).orElseThrow(() -> ApiException.noEncontrado("Jugador"));
        if (jugador.isBloqueado()) {
            throw ApiException.conflicto("Este jugador está retirado del mercado");
        }
        List<PlantillaJugador> plantilla = plantillas.findByEquipoConJugador(equipoId);
        if (plantilla.stream().anyMatch(p -> p.getJugador().getId().equals(jugadorId))) {
            throw ApiException.conflicto("Ya tienes a " + jugador.getNickname() + " en tu plantilla");
        }
        if (plantilla.size() >= ReglasJuego.MAX_JUGADORES_PLANTILLA) {
            throw ApiException.conflicto("Tu plantilla está completa (" + ReglasJuego.MAX_JUGADORES_PLANTILLA
                    + " jugadores). Vende a alguien primero.");
        }
        BigDecimal precio = jugador.getValorMercado();
        if (equipo.getDineroDisponible().compareTo(precio) < 0) {
            throw ApiException.conflicto("No tienes dinero suficiente para fichar a " + jugador.getNickname());
        }
        Set<Slot> ocupados = EnumSet.noneOf(Slot.class);
        plantilla.forEach(p -> ocupados.add(p.getSlot()));
        Slot slot = huecoLibre(jugador.getPosicion(), ocupados);

        LocalDateTime ahora = LocalDateTime.now();
        PlantillaJugador nueva = new PlantillaJugador();
        nueva.setEquipo(equipo);
        nueva.setJugador(jugador);
        nueva.colocarEn(slot);
        nueva.setPrecioCompra(precio);
        nueva.setFechaFichaje(ahora);
        plantillas.save(nueva);

        equipo.setDineroDisponible(equipo.getDineroDisponible().subtract(precio));
        transacciones.save(new Transaccion(equipo, jugador, TipoTransaccion.COMPRA, precio, ahora));
        return dto(equipo);
    }

    @Transactional
    public EquipoDto vender(AuthUser yo, Long equipoId, Long jugadorId) {
        EquipoFantasy equipo = propio(yo, equipoId);
        comprobarMercadoAbierto();
        PlantillaJugador p = plantillas.findByEquipoIdAndJugadorId(equipoId, jugadorId)
                .orElseThrow(() -> ApiException.conflicto("Ese jugador no está en tu plantilla"));
        JugadorReal jugador = p.getJugador();
        BigDecimal precio = jugador.getValorMercado();
        plantillas.delete(p);
        equipo.setDineroDisponible(equipo.getDineroDisponible().add(precio));
        transacciones.save(new Transaccion(equipo, jugador, TipoTransaccion.VENTA, precio, LocalDateTime.now()));
        plantillas.flush();
        return dto(equipo);
    }

    /**
     * Recoloca toda la plantilla. Cada jugador debe ocupar exactamente un hueco y
     * los huecos de titular solo admiten jugadores de ese rol.
     */
    @Transactional
    public EquipoDto alinear(AuthUser yo, Long equipoId, AlineacionRequest req) {
        EquipoFantasy equipo = propio(yo, equipoId);
        comprobarMercadoAbierto();
        Map<Long, PlantillaJugador> porJugador = new HashMap<>();
        plantillas.findByEquipoConJugador(equipoId).forEach(p -> porJugador.put(p.getJugador().getId(), p));

        Set<Long> asignados = new HashSet<>();
        for (Map.Entry<Slot, Long> entrada : req.slots().entrySet()) {
            Slot slot = entrada.getKey();
            Long jugadorId = entrada.getValue();
            if (jugadorId == null) {
                continue;
            }
            PlantillaJugador p = porJugador.get(jugadorId);
            if (p == null) {
                throw ApiException.peticionInvalida("El jugador " + jugadorId + " no está en tu plantilla");
            }
            if (!asignados.add(jugadorId)) {
                throw ApiException.peticionInvalida(p.getJugador().getNickname() + " está en dos huecos a la vez");
            }
            if (!slot.admite(p.getJugador().getPosicion())) {
                throw ApiException.peticionInvalida(p.getJugador().getNickname() + " es " + p.getJugador().getPosicion()
                        + " y no puede jugar de " + slot);
            }
        }
        if (asignados.size() != porJugador.size()) {
            throw ApiException.peticionInvalida("Todos los jugadores de tu plantilla deben tener un hueco");
        }
        req.slots().forEach((slot, jugadorId) -> {
            if (jugadorId != null) {
                porJugador.get(jugadorId).colocarEn(slot);
            }
        });
        return dto(equipo);
    }

    // ------------------------- Control de acceso -------------------------

    /** Equipo del usuario autenticado (para modificarlo). */
    public EquipoFantasy propio(AuthUser yo, Long equipoId) {
        EquipoFantasy e = equipos.findById(equipoId).orElseThrow(() -> ApiException.noEncontrado("Equipo"));
        if (!e.getUsuario().getId().equals(yo.id())) {
            throw ApiException.prohibido("Este equipo no es tuyo");
        }
        return e;
    }

    /** Un equipo lo pueden ver su dueño, los miembros de su liga y los administradores. */
    public EquipoFantasy visible(AuthUser yo, Long equipoId) {
        EquipoFantasy e = equipos.findById(equipoId).orElseThrow(() -> ApiException.noEncontrado("Equipo"));
        boolean propio = e.getUsuario().getId().equals(yo.id());
        if (!propio && !yo.esAdmin() && !equipos.existsByUsuarioIdAndGrupoId(yo.id(), e.getGrupo().getId())) {
            throw ApiException.prohibido("No perteneces a la liga de este equipo");
        }
        return e;
    }

    public void comprobarMercadoAbierto() {
        if (!mercadoAbierto()) {
            throw ApiException.conflicto("El mercado está cerrado mientras se juega la jornada");
        }
    }

    public boolean mercadoAbierto() {
        return !jornadas.existsByEstado(EstadoJornada.EN_CURSO);
    }

    // ------------------------- Auxiliares -------------------------

    static Slot huecoLibre(Posicion posicion, Set<Slot> ocupados) {
        Slot titular = Slot.titularDe(posicion);
        if (!ocupados.contains(titular)) {
            return titular;
        }
        for (Slot s : List.of(Slot.BANQUILLO_1, Slot.BANQUILLO_2)) {
            if (!ocupados.contains(s)) {
                return s;
            }
        }
        throw ApiException.conflicto("No hay hueco libre: el puesto de " + posicion
                + " y el banquillo están ocupados. Reorganiza tu alineación o vende a alguien.");
    }

    private EquipoDto dto(EquipoFantasy e) {
        Map<Long, JugadorService.Resumen> res = jugadorService.resumenes();
        List<PlantillaDto> plantilla = plantillas.findByEquipoConJugador(e.getId()).stream()
                .sorted(Comparator.comparingInt(p -> ORDEN_SLOTS.indexOf(p.getSlot())))
                .map(p -> new PlantillaDto(p.getSlot(), p.getEstado(), jugadorService.dto(p.getJugador(), res),
                        p.getPrecioCompra(), p.getFechaFichaje()))
                .toList();
        BigDecimal valor = plantilla.stream().map(p -> p.jugador().valorMercado()).reduce(BigDecimal.ZERO, BigDecimal::add);
        long posicion = equipos.posicionEnGrupo(e.getGrupo().getId(), e.getPuntosTotales());
        return new EquipoDto(e.getId(), e.getNombre(), e.getGrupo().getId(), e.getGrupo().getNombre(),
                e.getUsuario().getId(), e.getUsuario().getNombre(), e.getDineroDisponible(), e.getPuntosTotales(),
                posicion, valor, mercadoAbierto(), plantilla);
    }
}
