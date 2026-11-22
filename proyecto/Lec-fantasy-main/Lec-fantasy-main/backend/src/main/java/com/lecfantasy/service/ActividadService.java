package com.lecfantasy.service;

import com.lecfantasy.domain.RegistroActividad;
import com.lecfantasy.domain.TipoActividad;
import com.lecfantasy.repository.RegistroActividadRepository;
import com.lecfantasy.repository.UsuarioRepository;
import com.lecfantasy.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Registro de actividad de los administradores (panel "Actividad reciente"). */
@Service
public class ActividadService {

    private final RegistroActividadRepository repo;
    private final UsuarioRepository usuarios;

    public ActividadService(RegistroActividadRepository repo, UsuarioRepository usuarios) {
        this.repo = repo;
        this.usuarios = usuarios;
    }

    @Transactional
    public void registrar(AuthUser admin, TipoActividad tipo, String descripcion) {
        RegistroActividad a = new RegistroActividad();
        if (admin != null) {
            a.setAdmin(usuarios.getReferenceById(admin.id()));
            a.setAdminNombre(admin.nombre());
        } else {
            a.setAdminNombre("sistema");
        }
        a.setTipo(tipo);
        a.setDescripcion(descripcion.length() > 500 ? descripcion.substring(0, 500) : descripcion);
        a.setFecha(LocalDateTime.now());
        repo.save(a);
    }
}
