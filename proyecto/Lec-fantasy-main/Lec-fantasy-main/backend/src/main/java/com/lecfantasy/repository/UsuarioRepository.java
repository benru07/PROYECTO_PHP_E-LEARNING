package com.lecfantasy.repository;

import com.lecfantasy.domain.Rol;
import com.lecfantasy.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByNombreIgnoreCase(String nombre);

    Optional<Usuario> findByGoogleId(String googleId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByRol(Rol rol);

    long countByUltimoAccesoAfter(LocalDateTime desde);

    @Modifying
    @Transactional
    @Query("update Usuario u set u.ultimoAcceso = :momento where u.id = :id")
    void registrarAcceso(@Param("id") Long id, @Param("momento") LocalDateTime momento);

    /**
     * Búsqueda del panel de administración. estado: null = todos, true = solo baneados, false = solo activos.
     */
    @Query("""
            select u from Usuario u
            where (:texto is null
                   or lower(u.nombre) like lower(concat('%', :texto, '%'))
                   or lower(u.email) like lower(concat('%', :texto, '%'))
                   or str(u.id) = :texto)
              and (:pais is null or u.pais = :pais)
              and (:baneado is null
                   or (:baneado = true and exists (select b.id from Baneo b where b.usuario = u
                           and b.revocadoEn is null and (b.fin is null or b.fin > :ahora)))
                   or (:baneado = false and not exists (select b.id from Baneo b where b.usuario = u
                           and b.revocadoEn is null and (b.fin is null or b.fin > :ahora))))
            order by u.fechaRegistro desc
            """)
    Page<Usuario> buscar(@Param("texto") String texto,
                         @Param("pais") String pais,
                         @Param("baneado") Boolean baneado,
                         @Param("ahora") LocalDateTime ahora,
                         Pageable pageable);
}
