package com.lecfantasy.repository;

import com.lecfantasy.domain.Mejora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MejoraRepository extends JpaRepository<Mejora, Long> {

    List<Mejora> findByActivaTrueOrderByPrecioAsc();

    List<Mejora> findAllByOrderByIdAsc();
}
