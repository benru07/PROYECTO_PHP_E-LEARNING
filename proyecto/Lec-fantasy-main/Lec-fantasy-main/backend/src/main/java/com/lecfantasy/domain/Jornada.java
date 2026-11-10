package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jornada")
@Getter
@Setter
@NoArgsConstructor
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jornada")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoJornada estado = EstadoJornada.PROGRAMADA;

    /** OverviewPage de Leaguepedia, p. ej. "LEC/2026 Season/Spring Season". Opcional. */
    private String fuenteOverview;

    private LocalDateTime fechaFinalizacion;
}
