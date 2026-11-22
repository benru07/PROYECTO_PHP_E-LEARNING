package com.lecfantasy.repository;

import com.lecfantasy.domain.RegistroActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroActividadRepository extends JpaRepository<RegistroActividad, Long> {

    Page<RegistroActividad> findAllByOrderByFechaDesc(Pageable pageable);
}
