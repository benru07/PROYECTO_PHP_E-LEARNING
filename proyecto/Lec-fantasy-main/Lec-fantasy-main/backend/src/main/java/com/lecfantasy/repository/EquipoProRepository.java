package com.lecfantasy.repository;

import com.lecfantasy.domain.EquipoPro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipoProRepository extends JpaRepository<EquipoPro, Long> {

    Optional<EquipoPro> findByNombreIgnoreCase(String nombre);

    List<EquipoPro> findAllByOrderByNombreAsc();
}
