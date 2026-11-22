package com.lecfantasy.repository;

import com.lecfantasy.domain.EstadoMejora;
import com.lecfantasy.domain.MejoraAplicada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MejoraAplicadaRepository extends JpaRepository<MejoraAplicada, Long> {

    boolean existsByEquipoIdAndJugadorIdAndJornadaId(Long equipoId, Long jugadorId, Long jornadaId);

    @Query("""
            select m from MejoraAplicada m join fetch m.mejora join fetch m.jugador join fetch m.jornada
            where m.equipo.id = :equipoId order by m.jornada.fechaInicio desc, m.id desc
            """)
    List<MejoraAplicada> findByEquipo(@Param("equipoId") Long equipoId);

    @Query("""
            select m from MejoraAplicada m join fetch m.mejora join fetch m.jugador join fetch m.equipo
            where m.jornada.id = :jornadaId and m.estado = :estado
            """)
    List<MejoraAplicada> findByJornadaYEstado(@Param("jornadaId") Long jornadaId, @Param("estado") EstadoMejora estado);
}
