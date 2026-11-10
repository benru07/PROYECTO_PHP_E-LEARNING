package com.lecfantasy.security;

import com.lecfantasy.domain.Rol;

/** Usuario autenticado de la petición actual. Se obtiene con @AuthenticationPrincipal. */
public record AuthUser(Long id, String nombre, Rol rol) {

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }
}
