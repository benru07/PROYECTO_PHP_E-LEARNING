package com.lecfantasy.repository;

import com.lecfantasy.domain.Baneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BaneoRepository extends JpaRepository<Baneo, Long> {

    @Query("""
            select b from Baneo b where b.usuario.id = :usuarioId and b.revocadoEn is null
              and (b.fin is null or b.fin > :ahora) order by b.inicio desc
            """)
    List<Baneo> findActivos(@Param("usuarioId") Long usuarioId, @Param("ahora") LocalDateTime ahora);

    @Query("""
            select count(distinct b.usuario.id) from Baneo b where b.revocadoEn is null
              and (b.fin is null or b.fin > :ahora)
            """)
    long contarUsuariosBaneados(@Param("ahora") LocalDateTime ahora);

    @Query("select b from Baneo b where b.usuario.id = :usuarioId order by b.inicio desc")
    List<Baneo> historial(@Param("usuarioId") Long usuarioId);
}
