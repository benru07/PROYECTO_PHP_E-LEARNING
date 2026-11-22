package com.lecfantasy.dto;

import com.lecfantasy.domain.Rol;
import com.lecfantasy.domain.Tema;
import com.lecfantasy.domain.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    /** Datos del propio usuario (pantalla de perfil). */
    public record UsuarioPrivado(Long id, String nombre, String email, Rol rol, LocalDate fechaNacimiento,
                                 String pais, CatalogoDtos.EquipoProDto equipoFavorito, String riotId,
                                 boolean aceptaNoticias, boolean perfilCompleto, boolean tienePassword,
                                 LocalDateTime fechaRegistro, Tema tema, String colorAcento) {

        public static UsuarioPrivado of(Usuario u) {
            return new UsuarioPrivado(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getFechaNacimiento(),
                    u.getPais(), CatalogoDtos.EquipoProDto.ofNullable(u.getEquipoFavorito()), u.getRiotId(),
                    u.isAceptaNoticias(), u.isPerfilCompleto(), u.getPasswordHash() != null, u.getFechaRegistro(),
                    u.getTema(), u.getColorAcento());
        }
    }

    /** Lo que ve cualquier otro usuario. */
    public record UsuarioPublico(Long id, String nombre, String pais, CatalogoDtos.EquipoProDto equipoFavorito) {

        public static UsuarioPublico of(Usuario u) {
            return new UsuarioPublico(u.getId(), u.getNombre(), u.getPais(),
                    CatalogoDtos.EquipoProDto.ofNullable(u.getEquipoFavorito()));
        }
    }

    public record ActualizarPerfilRequest(
            @Pattern(regexp = "^[A-Za-z0-9_]{3,20}$", message = "Entre 3 y 20 caracteres: letras, números o _")
            String nombre,
            @Past LocalDate fechaNacimiento,
            @Pattern(regexp = "^[A-Za-z]{2}$", message = "Código de país ISO de 2 letras") String pais,
            Long equipoFavoritoId,
            @Pattern(regexp = "^$|^.{3,16}#[A-Za-z0-9]{2,5}$", message = "Formato Nombre#TAG") String riotId,
            Boolean aceptaNoticias) {
    }

    public record CambiarPasswordRequest(
            String passwordActual,
            @NotBlank @Size(min = 8, max = 72, message = "Mínimo 8 caracteres") String passwordNueva) {
    }

    /** Apariencia de la interfaz (selector de color de "Mi cuenta"). */
    public record AparienciaRequest(
            @NotNull(message = "Elige modo claro u oscuro") Tema tema,
            @NotBlank @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color en formato #RRGGBB") String colorAcento) {
    }
}
