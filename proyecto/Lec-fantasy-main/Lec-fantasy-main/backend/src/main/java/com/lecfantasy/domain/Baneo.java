package com.lecfantasy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "baneo")
@Getter
@Setter
@NoArgsConstructor
public class Baneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_baneo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin")
    private Usuario admin;

    @Column(nullable = false)
    private LocalDateTime inicio;

    /** null = baneo permanente. */
    private LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MotivoBaneo motivo;

    @Column(length = 500)
    private String comentario;

    private boolean notificado;

    private LocalDateTime revocadoEn;

    public boolean isPermanente() {
        return fin == null;
    }
}
