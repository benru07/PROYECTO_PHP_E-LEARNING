package com.lecfantasy.dto;

import com.lecfantasy.domain.Noticia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class NoticiaDtos {

    private NoticiaDtos() {
    }

    public record NoticiaDto(Long id, String titulo, String resumen, String contenido, String imagenUrl,
                             String autor, boolean publicada, LocalDateTime fechaPublicacion) {

        public static NoticiaDto of(Noticia n) {
            return new NoticiaDto(n.getId(), n.getTitulo(), n.getResumen(), n.getContenido(), n.getImagenUrl(),
                    n.getAutor() == null ? null : n.getAutor().getNombre(), n.isPublicada(), n.getFechaPublicacion());
        }
    }

    public record NoticiaRequest(
            @NotBlank(message = "El título es obligatorio") @Size(max = 200) String titulo,
            @Size(max = 500) String resumen,
            @NotBlank(message = "El contenido es obligatorio") @Size(max = 10000) String contenido,
            @Size(max = 500) String imagenUrl,
            boolean publicada) {
    }
}
