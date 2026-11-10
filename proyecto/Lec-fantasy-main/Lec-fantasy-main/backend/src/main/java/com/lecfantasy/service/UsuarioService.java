package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.domain.Usuario;
import com.lecfantasy.dto.UsuarioDtos.*;
import com.lecfantasy.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarios, AuthService authService, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UsuarioPrivado yo(Long id) {
        return UsuarioPrivado.of(obtener(id));
    }

    @Transactional(readOnly = true)
    public UsuarioPublico perfilPublico(Long id) {
        return UsuarioPublico.of(obtener(id));
    }

    @Transactional
    public UsuarioPrivado actualizar(Long id, ActualizarPerfilRequest req) {
        Usuario u = obtener(id);
        if (req.nombre() != null && !req.nombre().equalsIgnoreCase(u.getNombre())) {
            if (usuarios.existsByNombreIgnoreCase(req.nombre())) {
                throw ApiException.conflicto("Ese nombre de usuario ya está en uso");
            }
            u.setNombre(req.nombre());
        } else if (req.nombre() != null) {
            u.setNombre(req.nombre()); // permite cambiar solo mayúsculas/minúsculas
        }
        if (req.fechaNacimiento() != null) {
            authService.validarEdad(req.fechaNacimiento());
            u.setFechaNacimiento(req.fechaNacimiento());
        }
        if (req.pais() != null) {
            u.setPais(req.pais().toUpperCase(Locale.ROOT));
        }
        if (req.equipoFavoritoId() != null) {
            u.setEquipoFavorito(authService.buscarEquipoFavorito(req.equipoFavoritoId()));
        }
        if (req.riotId() != null) {
            u.setRiotId(AuthService.vacioANull(req.riotId()));
        }
        if (req.aceptaNoticias() != null) {
            u.setAceptaNoticias(req.aceptaNoticias());
        }
        return UsuarioPrivado.of(u);
    }

    @Transactional
    public UsuarioPrivado cambiarApariencia(Long id, AparienciaRequest req) {
        Usuario u = obtener(id);
        u.setTema(req.tema());
        u.setColorAcento(req.colorAcento().toUpperCase(Locale.ROOT));
        return UsuarioPrivado.of(u);
    }

    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest req) {
        Usuario u = obtener(id);
        // Las cuentas creadas con Google pueden definir su primera contraseña sin la actual
        if (u.getPasswordHash() != null
                && (req.passwordActual() == null || !passwordEncoder.matches(req.passwordActual(), u.getPasswordHash()))) {
            throw ApiException.peticionInvalida("La contraseña actual no es correcta");
        }
        u.setPasswordHash(passwordEncoder.encode(req.passwordNueva()));
    }

    private Usuario obtener(Long id) {
        return usuarios.findById(id).orElseThrow(() -> ApiException.noEncontrado("Usuario"));
    }
}
