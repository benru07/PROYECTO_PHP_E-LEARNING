package com.lecfantasy.repository;

import com.lecfantasy.domain.PlantillaJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlantillaJugadorRepository extends JpaRepository<PlantillaJugador, Long> {

    @Query("select p from PlantillaJugador p join fetch p.jugador j left join fetch j.equipoPro where p.equipo.id = :equipoId")
    List<PlantillaJugador> findByEquipoConJugador(@Param("equipoId") Long equipoId);

    Optional<PlantillaJugador> findByEquipoIdAndJugadorId(Long equipoId, Long jugadorId);

    boolean existsByEquipoIdAndJugadorId(Long equipoId, Long jugadorId);

    /** Todas las plantillas que contienen a un jugador (para retirarlo al bloquearlo). */
    @Query("select p from PlantillaJugador p join fetch p.equipo where p.jugador.id = :jugadorId")
    List<PlantillaJugador> findByJugadorConEquipo(@Param("jugadorId") Long jugadorId);

    @Query("select p from PlantillaJugador p join fetch p.equipo join fetch p.jugador")
    List<PlantillaJugador> findAllConEquipoYJugador();

    /** [idEquipo, suma del valor de mercado] de cada equipo de una liga. */
    @Query("select p.equipo.id, sum(p.jugador.valorMercado) from PlantillaJugador p where p.equipo.grupo.id = :grupoId group by p.equipo.id")
    List<Object[]> valorPlantillasPorGrupo(@Param("grupoId") Long grupoId);
}
