package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grupo_liga")
@Getter
@Setter
@NoArgsConstructor
public class GrupoLiga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoLiga tipo;

    @Column(nullable = false, length = 12)
    private String codigoInvitacion;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal presupuestoInicial;

    @Column(nullable = false)
    private int maxParticipantes;

    private boolean esGlobal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_creador")
    private Usuario creador;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}
