package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "estadistica_jugador_partida")
@Getter
@Setter
@NoArgsConstructor
public class EstadisticaJugadorPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadistica")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_partida")
    private PartidaReal partida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jugador_real")
    private JugadorReal jugador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_pro")
    private EquipoPro equipoPro;

    private String campeon;
    private int kills;
    private int deaths;
    private int assists;
    private int cs;
    private int visionScore;
    private int doubleKills;
    private int tripleKills;
    private int quadraKills;
    private int pentaKills;
    private boolean firstBlood;
    private boolean victoria;
    private boolean mvp;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal puntosFantasy = BigDecimal.ZERO;
}
