package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "partida_real")
@Getter
@Setter
@NoArgsConstructor
public class PartidaReal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partida")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada")
    private Jornada jornada;

    @Column(nullable = false, length = 150)
    private String idExterno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_azul")
    private EquipoPro equipoAzul;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_rojo")
    private EquipoPro equipoRojo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ganador")
    private EquipoPro ganador;

    private Integer duracionSegundos;

    @Column(nullable = false)
    private LocalDateTime fechaHora;
}
