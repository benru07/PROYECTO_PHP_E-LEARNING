package com.lecfantasy.repository;

import com.lecfantasy.domain.EstadoJornada;
import com.lecfantasy.domain.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    boolean existsByEstado(EstadoJornada estado);

    List<Jornada> findAllByOrderByFechaInicioAsc();

    Optional<Jornada> findFirstByEstadoOrderByFechaInicioAsc(EstadoJornada estado);
}
