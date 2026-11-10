package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipo_fantasy")
@Getter
@Setter
@NoArgsConstructor
public class EquipoFantasy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo_fantasy")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grupo")
    private GrupoLiga grupo;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal dineroDisponible;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal puntosTotales = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime fechaUnion;

    /** Bloqueo optimista: evita gastar el mismo dinero en dos peticiones simultáneas. */
    @Version
    private Long version;
}
