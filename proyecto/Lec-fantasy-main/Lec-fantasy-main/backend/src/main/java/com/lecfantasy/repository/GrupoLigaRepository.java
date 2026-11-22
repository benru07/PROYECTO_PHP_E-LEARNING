package com.lecfantasy.repository;

import com.lecfantasy.domain.GrupoLiga;
import com.lecfantasy.domain.TipoLiga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrupoLigaRepository extends JpaRepository<GrupoLiga, Long> {

    Optional<GrupoLiga> findByCodigoInvitacion(String codigo);

    Optional<GrupoLiga> findFirstByEsGlobalTrue();

    boolean existsByCodigoInvitacion(String codigo);

    List<GrupoLiga> findByTipoOrderByFechaCreacionDesc(TipoLiga tipo);
}
