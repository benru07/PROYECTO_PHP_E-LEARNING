package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.*;
import com.lecfantasy.dto.CatalogoDtos.*;
import com.lecfantasy.repository.*;
import com.lecfantasy.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Jugadores reales (mercado) y equipos profesionales, parte pública y de administración. */
@Service
public class JugadorService {

    private final JugadorRealRepository jugadores;
    private final EquipoProRepository equiposPro;
    private final EstadisticaRepository estadisticas;
    private final PlantillaJugadorRepository plantillas;
    private final TransaccionRepository transacciones;
    private final HistorialValorRepository historialValor;
    private final ActividadService actividad;

    public JugadorService(JugadorRealRepository jugadores, EquipoProRepository equiposPro,
                          EstadisticaRepository estadisticas, PlantillaJugadorRepository plantillas,
                          TransaccionRepository transacciones, HistorialValorRepository historialValor,
                          ActividadService actividad) {
        this.historialValor = historialValor;
        this.jugadores = jugadores;
        this.equiposPro = equiposPro;
        this.estadisticas = estadisticas;
        this.plantillas = plantillas;
        this.transacciones = transacciones;
        this.actividad = actividad;
    }

    /** Puntos totales y partidas jugadas de cada jugador. */
    public record Resumen(BigDecimal puntos, long partidas) {
        public static final Resumen VACIO = new Resumen(BigDecimal.ZERO, 0);
    }

    @Transactional(readOnly = true)
    public Map<Long, Resumen> resumenes() {
        Map<Long, Resumen> mapa = new HashMap<>();
        for (Object[] fila : estadisticas.resumenPorJugador()) {
            Long id = ((Number) fila[0]).longValue();
            BigDecimal puntos = fila[1] instanceof BigDecimal b ? b : new BigDecimal(String.valueOf(fila[1]));
            mapa.put(id, new Resumen(puntos, ((Number) fila[2]).longValue()));
        }
        return mapa;
    }

    public JugadorDto dto(JugadorReal j, Map<Long, Resumen> resumenes) {
        Resumen r = resumenes.getOrDefault(j.getId(), Resumen.VACIO);
        return JugadorDto.of(j, r.puntos(), r.partidas());
    }

    // ------------------------- Público -------------------------

    @Transactional(readOnly = true)
    public List<JugadorDto> buscar(Posicion posicion, Long equipoId, String texto, boolean incluirBloqueados) {
        Map<Long, Resumen> res = resumenes();
        String t = texto == null || texto.isBlank() ? null : texto.trim();
        return jugadores.buscar(posicion, equipoId, t, incluirBloqueados).stream().map(j -> dto(j, res)).toList();
    }

    @Transactional(readOnly = true)
    public JugadorDetalleDto detalle(Long id) {
        JugadorReal j = obtener(id);
        List<EstadisticaDto> historial = estadisticas.findByJugador(id).stream().map(EstadisticaDto::of).toList();
        BigDecimal puntos = historial.stream().map(EstadisticaDto::puntos).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<ValorHistoricoDto> valores = historialValor.findByJugador(id).stream().map(ValorHistoricoDto::of).toList();
        return new JugadorDetalleDto(JugadorDto.of(j, puntos, historial.size()), historial, valores);
    }

    @Transactional(readOnly = true)
    public List<EquipoProDto> equiposPro() {
        return equiposPro.findAllByOrderByNombreAsc().stream().map(EquipoProDto::ofNullable).toList();
    }

    // ------------------------- Administración -------------------------

    @Transactional
    public JugadorDto crear(AuthUser admin, JugadorRequest req) {
        if (jugadores.findByNicknameIgnoreCase(req.nickname().trim()).isPresent()) {
            throw ApiException.conflicto("Ya existe un jugador con ese nickname");
        }
        JugadorReal j = new JugadorReal();
        aplicar(j, req);
        j.setValorMercado(precio(req));
        jugadores.save(j);
        actividad.registrar(admin, TipoActividad.CREAR_JUGADOR, admin.nombre() + " creó el jugador «" + j.getNickname() + "»");
        return dto(j, resumenes());
    }

    @Transactional
    public JugadorDto editar(AuthUser admin, Long id, JugadorRequest req) {
        JugadorReal j = obtener(id);
        jugadores.findByNicknameIgnoreCase(req.nickname().trim())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw ApiException.conflicto("Ya existe un jugador con ese nickname");
                });
        BigDecimal valorAnterior = j.getValorMercado();
        aplicar(j, req);
        String cambioPrecio = "";
        if (j.cambiarValor(precio(req))) {
            historialValor.registrarCambio(j, null, LocalDateTime.now());
            cambioPrecio = " (precio " + valorAnterior.toPlainString() + " → " + j.getValorMercado().toPlainString() + ")";
        }
        actividad.registrar(admin, TipoActividad.EDITAR_JUGADOR,
                admin.nombre() + " editó al jugador «" + j.getNickname() + "»" + cambioPrecio);
        return dto(j, resumenes());
    }

    /**
     * Bloquea a un jugador: sale del mercado y se retira de todas las plantillas,
     * devolviendo a cada mánager su valor de mercado actual.
     */
    @Transactional
    public JugadorDto bloquear(AuthUser admin, Long id, String motivo) {
        JugadorReal j = obtener(id);
        if (j.isBloqueado()) {
            throw ApiException.conflicto("El jugador ya está bloqueado");
        }
        j.setBloqueado(true);
        j.setMotivoBloqueo(motivo);
        LocalDateTime ahora = LocalDateTime.now();
        List<PlantillaJugador> enPlantillas = plantillas.findByJugadorConEquipo(id);
        for (PlantillaJugador p : enPlantillas) {
            EquipoFantasy e = p.getEquipo();
            e.setDineroDisponible(e.getDineroDisponible().add(j.getValorMercado()));
            transacciones.save(new Transaccion(e, j, TipoTransaccion.VENTA, j.getValorMercado(), ahora));
            plantillas.delete(p);
        }
        actividad.registrar(admin, TipoActividad.BLOQUEO_JUGADOR, admin.nombre() + " bloqueó al jugador «"
                + j.getNickname() + "» (" + motivo + "). Retirado de " + enPlantillas.size() + " equipos.");
        return dto(j, resumenes());
    }

    @Transactional
    public JugadorDto desbloquear(AuthUser admin, Long id) {
        JugadorReal j = obtener(id);
        j.setBloqueado(false);
        j.setMotivoBloqueo(null);
        actividad.registrar(admin, TipoActividad.DESBLOQUEO_JUGADOR,
                admin.nombre() + " desbloqueó al jugador «" + j.getNickname() + "»");
        return dto(j, resumenes());
    }

    @Transactional
    public EquipoProDto crearEquipoPro(AuthUser admin, EquipoProRequest req) {
        if (equiposPro.findByNombreIgnoreCase(req.nombre().trim()).isPresent()) {
            throw ApiException.conflicto("Ya existe un equipo con ese nombre");
        }
        EquipoPro e = new EquipoPro(req.nombre().trim());
        e.setSiglas(AuthService.vacioANull(req.siglas()));
        e.setLogoUrl(AuthService.vacioANull(req.logoUrl()));
        equiposPro.save(e);
        actividad.registrar(admin, TipoActividad.EQUIPO_PRO, admin.nombre() + " creó el equipo «" + e.getNombre() + "»");
        return EquipoProDto.ofNullable(e);
    }

    @Transactional
    public EquipoProDto editarEquipoPro(AuthUser admin, Long id, EquipoProRequest req) {
        EquipoPro e = equiposPro.findById(id).orElseThrow(() -> ApiException.noEncontrado("Equipo"));
        equiposPro.findByNombreIgnoreCase(req.nombre().trim())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw ApiException.conflicto("Ya existe un equipo con ese nombre");
                });
        e.setNombre(req.nombre().trim());
        e.setSiglas(AuthService.vacioANull(req.siglas()));
        e.setLogoUrl(AuthService.vacioANull(req.logoUrl()));
        actividad.registrar(admin, TipoActividad.EQUIPO_PRO, admin.nombre() + " editó el equipo «" + e.getNombre() + "»");
        return EquipoProDto.ofNullable(e);
    }

    private void aplicar(JugadorReal j, JugadorRequest req) {
        j.setNickname(req.nickname().trim());
        j.setNombreReal(AuthService.vacioANull(req.nombreReal()));
        j.setPosicion(req.posicion());
        j.setEquipoPro(req.equipoId() == null ? null
                : equiposPro.findById(req.equipoId()).orElseThrow(() -> ApiException.peticionInvalida("Equipo no válido")));
        j.setFotoUrl(AuthService.vacioANull(req.fotoUrl()));
    }

    private static BigDecimal precio(JugadorRequest req) {
        return req.valorMercado().setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private JugadorReal obtener(Long id) {
        return jugadores.findById(id).orElseThrow(() -> ApiException.noEncontrado("Jugador"));
    }

    static String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}
