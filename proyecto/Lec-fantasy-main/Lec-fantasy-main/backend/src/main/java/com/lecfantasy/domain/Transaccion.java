package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion")
@Getter
@Setter
@NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equipo_fantasy")
    private EquipoFantasy equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jugador_real")
    private JugadorReal jugador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoTransaccion tipo;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Transaccion(EquipoFantasy equipo, JugadorReal jugador, TipoTransaccion tipo, BigDecimal precio, LocalDateTime fecha) {
        this.equipo = equipo;
        this.jugador = jugador;
        this.tipo = tipo;
        this.precio = precio;
        this.fecha = fecha;
    }
}
