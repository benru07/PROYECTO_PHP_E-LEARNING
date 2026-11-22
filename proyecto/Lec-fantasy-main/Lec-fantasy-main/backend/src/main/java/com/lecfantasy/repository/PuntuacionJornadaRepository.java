package com.lecfantasy.repository;

import com.lecfantasy.domain.PuntuacionJornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PuntuacionJornadaRepository extends JpaRepository<PuntuacionJornada, Long> {

    @Query("""
            select p from PuntuacionJornada p join fetch p.equipo e join fetch e.usuario
            where e.grupo.id = :grupoId and p.jornada.id = :jornadaId
            order by p.puntosTotales desc
            """)
    List<PuntuacionJornada> clasificacionJornada(@Param("grupoId") Long grupoId, @Param("jornadaId") Long jornadaId);

    @Query("select p from PuntuacionJornada p join fetch p.jornada j where p.equipo.id = :equipoId order by j.fechaInicio asc")
    List<PuntuacionJornada> findByEquipo(@Param("equipoId") Long equipoId);

    /** Puntuaciones de todos los equipos de una liga (para la racha de la clasificación). */
    @Query("select p from PuntuacionJornada p join fetch p.jornada j where p.equipo.grupo.id = :grupoId order by j.fechaInicio asc")
    List<PuntuacionJornada> findByGrupo(@Param("grupoId") Long grupoId);
}
