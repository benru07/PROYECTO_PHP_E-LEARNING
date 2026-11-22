package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plantilla_jugador")
@Getter
@Setter
@NoArgsConstructor
public class PlantillaJugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equipo_fantasy")
    private EquipoFantasy equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jugador_real")
    private JugadorReal jugador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoPlantilla estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicion_slot", nullable = false, length = 12)
    private Slot slot;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal precioCompra;

    @Column(nullable = false)
    private LocalDateTime fechaFichaje;

    public void colocarEn(Slot nuevoSlot) {
        this.slot = nuevoSlot;
        this.estado = nuevoSlot.esTitular() ? EstadoPlantilla.TITULAR : EstadoPlantilla.BANQUILLO;
    }
}
