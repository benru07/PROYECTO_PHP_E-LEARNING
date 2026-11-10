package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "mejora")
@Getter
@Setter
@NoArgsConstructor
public class Mejora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mejora")
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    private String descripcion;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int multiplicadorBono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CondicionMejora condicion;

    @Column(precision = 6, scale = 2)
    private BigDecimal umbral;

    private boolean activa = true;
}
