package com.lecfantasy.repository;

import com.lecfantasy.domain.PartidaReal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartidaRealRepository extends JpaRepository<PartidaReal, Long> {

    boolean existsByIdExterno(String idExterno);

    @Query("""
            select p from PartidaReal p
            left join fetch p.equipoAzul left join fetch p.equipoRojo left join fetch p.ganador
            where p.jornada.id = :jornadaId order by p.fechaHora asc
            """)
    List<PartidaReal> findByJornada(@Param("jornadaId") Long jornadaId);
}
