package com.lecfantasy.repository;

import com.lecfantasy.domain.EquipoFantasy;
import com.lecfantasy.domain.GrupoLiga;
import com.lecfantasy.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EquipoFantasyRepository extends JpaRepository<EquipoFantasy, Long> {

    @Query("select e from EquipoFantasy e join fetch e.usuario where e.grupo.id = :grupoId order by e.puntosTotales desc, e.fechaUnion asc")
    List<EquipoFantasy> clasificacion(@Param("grupoId") Long grupoId);

    @Query("select e from EquipoFantasy e join fetch e.grupo where e.usuario.id = :usuarioId order by e.fechaUnion asc")
    List<EquipoFantasy> findByUsuarioConGrupo(@Param("usuarioId") Long usuarioId);

    Optional<EquipoFantasy> findByUsuarioAndGrupo(Usuario usuario, GrupoLiga grupo);

    Optional<EquipoFantasy> findByUsuarioIdAndGrupoId(Long usuarioId, Long grupoId);

    boolean existsByUsuarioIdAndGrupoId(Long usuarioId, Long grupoId);

    long countByGrupoId(Long grupoId);

    /** Posición (1, 2, 3...) de un equipo dentro de su liga. */
    @Query("select count(e) + 1 from EquipoFantasy e where e.grupo.id = :grupoId and e.puntosTotales > :puntos")
    long posicionEnGrupo(@Param("grupoId") Long grupoId, @Param("puntos") BigDecimal puntos);
}
