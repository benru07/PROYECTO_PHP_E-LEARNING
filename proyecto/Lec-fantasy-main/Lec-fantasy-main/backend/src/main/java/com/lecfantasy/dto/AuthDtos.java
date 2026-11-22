package com.lecfantasy.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegistroRequest(
            @NotBlank(message = "El nombre de usuario es obligatorio")
            @Pattern(regexp = "^[A-Za-z0-9_]{3,20}$", message = "Entre 3 y 20 caracteres: letras, números o _")
            String nombre,
            @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo no válido") @Size(max = 255)
            String email,
            @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, max = 72, message = "Mínimo 8 caracteres")
            String password,
            @NotBlank(message = "Repite la contraseña")
            String repetirPassword,
            @NotNull(message = "La fecha de nacimiento es obligatoria") @Past(message = "Fecha no válida")
            LocalDate fechaNacimiento,
            @NotBlank(message = "El país es obligatorio") @Pattern(regexp = "^[A-Za-z]{2}$", message = "Código de país ISO de 2 letras")
            String pais,
            Long equipoFavoritoId,
            @Pattern(regexp = "^$|^.{3,16}#[A-Za-z0-9]{2,5}$", message = "Formato Nombre#TAG")
            String riotId,
            boolean aceptaTerminos,
            boolean aceptaNoticias) {
    }

    public record LoginRequest(
            @NotBlank(message = "Introduce tu usuario o email") String identificador,
            @NotBlank(message = "Introduce tu contraseña") String password,
            boolean recordarme) {
    }

    public record GoogleLoginRequest(@NotBlank String idToken) {
    }

    public record OlvidoPasswordRequest(@NotBlank @Email String email) {
    }

    public record RestablecerPasswordRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 8, max = 72, message = "Mínimo 8 caracteres") String password) {
    }

    public record AuthResponse(String token, Instant expira, UsuarioDtos.UsuarioPrivado usuario) {
    }
}
