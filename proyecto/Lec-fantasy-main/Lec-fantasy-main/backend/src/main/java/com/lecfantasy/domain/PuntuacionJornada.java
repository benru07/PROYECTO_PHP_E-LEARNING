package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "puntuacion_jornada")
@Getter
@Setter
@NoArgsConstructor
public class PuntuacionJornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puntuacion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equipo_fantasy")
    private EquipoFantasy equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada")
    private Jornada jornada;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal puntosBase;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal puntosMejoras;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal puntosTotales;
}
