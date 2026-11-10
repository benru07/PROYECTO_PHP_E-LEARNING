package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equipo_pro")
@Getter
@Setter
@NoArgsConstructor
public class EquipoPro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo_pro")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 10)
    private String siglas;

    private String logoUrl;

    public EquipoPro(String nombre) {
        this.nombre = nombre;
    }
}
