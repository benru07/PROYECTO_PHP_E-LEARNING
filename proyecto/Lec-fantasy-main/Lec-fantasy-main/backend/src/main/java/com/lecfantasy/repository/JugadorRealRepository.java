package com.lecfantasy.repository;

import com.lecfantasy.domain.JugadorReal;
import com.lecfantasy.domain.Posicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JugadorRealRepository extends JpaRepository<JugadorReal, Long> {

    Optional<JugadorReal> findByNicknameIgnoreCase(String nickname);

    Optional<JugadorReal> findByIdExterno(String idExterno);

    long countByBloqueadoTrue();

    @Query("""
            select j from JugadorReal j left join fetch j.equipoPro ep
            where (:incluirBloqueados = true or j.bloqueado = false)
              and (:posicion is null or j.posicion = :posicion)
              and (:equipoId is null or ep.id = :equipoId)
              and (:texto is null or lower(j.nickname) like lower(concat('%', :texto, '%')))
            order by j.valorMercado desc, j.nickname asc
            """)
    List<JugadorReal> buscar(@Param("posicion") Posicion posicion,
                             @Param("equipoId") Long equipoId,
                             @Param("texto") String texto,
                             @Param("incluirBloqueados") boolean incluirBloqueados);
}
