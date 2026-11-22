package com.lecfantasy.repository;

import com.lecfantasy.domain.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("select t from Transaccion t join fetch t.jugador where t.equipo.id = :equipoId order by t.fecha desc, t.id desc")
    List<Transaccion> findByEquipo(@Param("equipoId") Long equipoId);
}
