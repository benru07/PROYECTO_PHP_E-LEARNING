package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    /** Naranja LEC: el acento con el que empieza cualquier cuenta. */
    public static final String COLOR_ACENTO_POR_DEFECTO = "#FF6B1A";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false)
    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Rol rol = Rol.USER;

    private LocalDate fechaNacimiento;

    @Column(length = 2)
    private String pais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_favorito")
    private EquipoPro equipoFavorito;

    private String riotId;

    private boolean aceptaNoticias;

    private String googleId;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    private LocalDateTime ultimoAcceso;

    /** Apariencia elegida en "Mi cuenta": OSCURO o CLARO. La comparten la web y la app. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Tema tema = Tema.OSCURO;

    /** Color de acento en hexadecimal (#RRGGBB). */
    @Column(nullable = false, length = 7)
    private String colorAcento = COLOR_ACENTO_POR_DEFECTO;

    /** Los usuarios que entran con Google deben completar fecha de nacimiento y país. */
    public boolean isPerfilCompleto() {
        return fechaNacimiento != null && pais != null;
    }
}
