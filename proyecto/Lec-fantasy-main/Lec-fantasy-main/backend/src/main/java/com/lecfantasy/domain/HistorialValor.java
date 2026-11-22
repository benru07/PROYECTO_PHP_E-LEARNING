package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Valor de mercado de un jugador tras cada cambio de precio. */
@Entity
@Table(name = "historial_valor")
@Getter
@Setter
@NoArgsConstructor
public class HistorialValor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jugador_real")
    private JugadorReal jugador;

    /** Jornada cuyo cierre provocó el cambio; null si lo cambió un administrador. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jornada")
    private Jornada jornada;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public HistorialValor(JugadorReal jugador, Jornada jornada, BigDecimal valor, LocalDateTime fecha) {
        this.jugador = jugador;
        this.jornada = jornada;
        this.valor = valor;
        this.fecha = fecha;
    }
}
