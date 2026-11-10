package com.lecfantasy.security;

import com.lecfantasy.domain.Usuario;
import com.lecfantasy.repository.BaneoRepository;
import com.lecfantasy.repository.UsuarioRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Convierte el JWT en el usuario autenticado. Consulta la BD en cada petición para que
 * un baneo, un cambio de rol o un borrado tengan efecto inmediato aunque el token siga vigente.
 * No es un @Component a propósito: si lo fuera, Spring MVC lo registraría como conversor general.
 */
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarios;
    private final BaneoRepository baneos;

    public JwtAuthConverter(UsuarioRepository usuarios, BaneoRepository baneos) {
        this.usuarios = usuarios;
        this.baneos = baneos;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Long id;
        try {
            id = Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new InvalidBearerTokenException("Token no válido");
        }
        Usuario usuario = usuarios.findById(id)
                .orElseThrow(() -> new InvalidBearerTokenException("El usuario ya no existe"));
        LocalDateTime ahora = LocalDateTime.now();
        if (!baneos.findActivos(id, ahora).isEmpty()) {
            throw new InvalidBearerTokenException("Cuenta baneada");
        }
        if (usuario.getUltimoAcceso() == null || usuario.getUltimoAcceso().isBefore(ahora.minusMinutes(5))) {
            usuarios.registrarAcceso(id, ahora);
        }
        AuthUser principal = new AuthUser(usuario.getId(), usuario.getNombre(), usuario.getRol());
        return new UsernamePasswordAuthenticationToken(principal, jwt,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
    }
}
