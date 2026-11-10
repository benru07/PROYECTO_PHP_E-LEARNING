package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "mejora_aplicada")
@Getter
@Setter
@NoArgsConstructor
public class MejoraAplicada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mejora_aplicada")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equipo_fantasy")
    private EquipoFantasy equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jugador_real")
    private JugadorReal jugador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mejora")
    private Mejora mejora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada")
    private Jornada jornada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoMejora estado = EstadoMejora.PENDIENTE;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal precioPagado;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal puntosExtra = BigDecimal.ZERO;
}
