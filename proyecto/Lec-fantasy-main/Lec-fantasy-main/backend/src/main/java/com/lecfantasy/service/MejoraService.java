package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.*;
import com.lecfantasy.dto.CatalogoDtos.MejoraDto;
import com.lecfantasy.dto.CatalogoDtos.MejoraRequest;
import com.lecfantasy.dto.EquipoDtos.ComprarMejoraRequest;
import com.lecfantasy.dto.EquipoDtos.MejoraAplicadaDto;
import com.lecfantasy.repository.*;
import com.lecfantasy.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;

/**
 * Mejoras: se compran para un jugador de tu plantilla y se aplican a la próxima jornada programada.
 * Si la condición se cumple (y el jugador es titular), sus puntos se multiplican.
 */
@Service
public class MejoraService {

    private final MejoraRepository mejoras;
    private final MejoraAplicadaRepository aplicadas;
    private final JornadaRepository jornadas;
    private final PlantillaJugadorRepository plantillas;
    private final EquipoService equipoService;
    private final ActividadService actividad;

    public MejoraService(MejoraRepository mejoras, MejoraAplicadaRepository aplicadas, JornadaRepository jornadas,
                         PlantillaJugadorRepository plantillas, EquipoService equipoService,
                         ActividadService actividad) {
        this.mejoras = mejoras;
        this.aplicadas = aplicadas;
        this.jornadas = jornadas;
        this.plantillas = plantillas;
        this.equipoService = equipoService;
        this.actividad = actividad;
    }

    @Transactional(readOnly = true)
    public List<MejoraDto> catalogo(boolean incluirInactivas) {
        return (incluirInactivas ? mejoras.findAllByOrderByIdAsc() : mejoras.findByActivaTrueOrderByPrecioAsc())
                .stream().map(MejoraDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<MejoraAplicadaDto> delEquipo(AuthUser yo, Long equipoId) {
        equipoService.visible(yo, equipoId);
        return aplicadas.findByEquipo(equipoId).stream().map(MejoraAplicadaDto::of).toList();
    }

    @Transactional
    public MejoraAplicadaDto comprar(AuthUser yo, Long equipoId, ComprarMejoraRequest req) {
        EquipoFantasy equipo = equipoService.propio(yo, equipoId);
        equipoService.comprobarMercadoAbierto();
        Jornada jornada = jornadas.findFirstByEstadoOrderByFechaInicioAsc(EstadoJornada.PROGRAMADA)
                .orElseThrow(() -> ApiException.conflicto("No hay ninguna jornada programada a la que aplicar la mejora"));
        Mejora mejora = mejoras.findById(req.mejoraId()).filter(Mejora::isActiva)
                .orElseThrow(() -> ApiException.noEncontrado("Mejora"));
        PlantillaJugador p = plantillas.findByEquipoIdAndJugadorId(equipoId, req.jugadorId())
                .orElseThrow(() -> ApiException.conflicto("Ese jugador no está en tu plantilla"));
        if (aplicadas.existsByEquipoIdAndJugadorIdAndJornadaId(equipoId, req.jugadorId(), jornada.getId())) {
            throw ApiException.conflicto(p.getJugador().getNickname() + " ya tiene una mejora para " + jornada.getNombre());
        }
        if (equipo.getDineroDisponible().compareTo(mejora.getPrecio()) < 0) {
            throw ApiException.conflicto("No tienes dinero suficiente para esta mejora");
        }
        equipo.setDineroDisponible(equipo.getDineroDisponible().subtract(mejora.getPrecio()));
        MejoraAplicada m = new MejoraAplicada();
        m.setEquipo(equipo);
        m.setJugador(p.getJugador());
        m.setMejora(mejora);
        m.setJornada(jornada);
        m.setPrecioPagado(mejora.getPrecio());
        aplicadas.save(m);
        return MejoraAplicadaDto.of(m);
    }

    /** Cancela una mejora pendiente y devuelve el dinero (solo con el mercado abierto). */
    @Transactional
    public void cancelar(AuthUser yo, Long equipoId, Long mejoraAplicadaId) {
        EquipoFantasy equipo = equipoService.propio(yo, equipoId);
        equipoService.comprobarMercadoAbierto();
        MejoraAplicada m = aplicadas.findById(mejoraAplicadaId)
                .filter(x -> x.getEquipo().getId().equals(equipoId))
                .orElseThrow(() -> ApiException.noEncontrado("Mejora"));
        if (m.getEstado() != EstadoMejora.PENDIENTE || m.getJornada().getEstado() != EstadoJornada.PROGRAMADA) {
            throw ApiException.conflicto("Esta mejora ya no se puede cancelar");
        }
        equipo.setDineroDisponible(equipo.getDineroDisponible().add(m.getPrecioPagado()));
        aplicadas.delete(m);
    }

    // ------------------------- Administración -------------------------

    @Transactional
    public MejoraDto crear(AuthUser admin, MejoraRequest req) {
        Mejora m = new Mejora();
        aplicar(m, req);
        mejoras.save(m);
        actividad.registrar(admin, TipoActividad.MEJORA, admin.nombre() + " creó la mejora «" + m.getNombre() + "»");
        return MejoraDto.of(m);
    }

    @Transactional
    public MejoraDto editar(AuthUser admin, Long id, MejoraRequest req) {
        Mejora m = mejoras.findById(id).orElseThrow(() -> ApiException.noEncontrado("Mejora"));
        aplicar(m, req);
        actividad.registrar(admin, TipoActividad.MEJORA, admin.nombre() + " editó la mejora «" + m.getNombre() + "»");
        return MejoraDto.of(m);
    }

    private static void aplicar(Mejora m, MejoraRequest req) {
        if (req.condicion() == CondicionMejora.KDA_MINIMO && req.umbral() == null) {
            throw ApiException.peticionInvalida("La condición KDA_MINIMO necesita un umbral");
        }
        m.setNombre(req.nombre().trim());
        m.setDescripcion(req.descripcion());
        m.setPrecio(req.precio().setScale(2, RoundingMode.HALF_UP));
        m.setMultiplicadorBono(req.multiplicadorBono());
        m.setCondicion(req.condicion());
        m.setUmbral(req.umbral());
        m.setActiva(req.activa());
    }
}
