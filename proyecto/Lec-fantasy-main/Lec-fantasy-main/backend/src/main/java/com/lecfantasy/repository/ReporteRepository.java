package com.lecfantasy.repository;

import com.lecfantasy.domain.EstadoReporte;
import com.lecfantasy.domain.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    long countByEstado(EstadoReporte estado);

    long countByReportadoId(Long usuarioId);

    @Query("""
            select r from Reporte r join fetch r.reportador join fetch r.reportado
            where (:estado is null or r.estado = :estado) order by r.fecha desc
            """)
    List<Reporte> buscar(@Param("estado") EstadoReporte estado);
}
