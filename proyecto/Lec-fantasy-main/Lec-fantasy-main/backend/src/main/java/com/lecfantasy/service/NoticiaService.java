package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.common.PageResponse;
import com.lecfantasy.domain.Noticia;
import com.lecfantasy.domain.TipoActividad;
import com.lecfantasy.dto.NoticiaDtos.NoticiaDto;
import com.lecfantasy.dto.NoticiaDtos.NoticiaRequest;
import com.lecfantasy.repository.NoticiaRepository;
import com.lecfantasy.repository.UsuarioRepository;
import com.lecfantasy.security.AuthUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NoticiaService {

    private final NoticiaRepository noticias;
    private final UsuarioRepository usuarios;
    private final ActividadService actividad;

    public NoticiaService(NoticiaRepository noticias, UsuarioRepository usuarios, ActividadService actividad) {
        this.noticias = noticias;
        this.usuarios = usuarios;
        this.actividad = actividad;
    }

    @Transactional(readOnly = true)
    public PageResponse<NoticiaDto> publicadas(int pagina, int tamano) {
        return PageResponse.of(noticias.findByPublicadaTrueOrderByFechaPublicacionDesc(
                PageRequest.of(Math.max(0, pagina), limitar(tamano))), NoticiaDto::of);
    }

    @Transactional(readOnly = true)
    public PageResponse<NoticiaDto> todas(int pagina, int tamano) {
        return PageResponse.of(noticias.findAllByOrderByFechaPublicacionDesc(
                PageRequest.of(Math.max(0, pagina), limitar(tamano))), NoticiaDto::of);
    }

    @Transactional(readOnly = true)
    public NoticiaDto ver(Long id, boolean esAdmin) {
        Noticia n = obtener(id);
        if (!n.isPublicada() && !esAdmin) {
            throw ApiException.noEncontrado("Noticia");
        }
        return NoticiaDto.of(n);
    }

    @Transactional
    public NoticiaDto crear(AuthUser admin, NoticiaRequest req) {
        Noticia n = new Noticia();
        aplicar(n, req);
        n.setAutor(usuarios.getReferenceById(admin.id()));
        n.setFechaPublicacion(LocalDateTime.now());
        noticias.save(n);
        actividad.registrar(admin, TipoActividad.NOTICIA, admin.nombre() + " publicó una noticia: «" + n.getTitulo() + "»");
        return NoticiaDto.of(n);
    }

    @Transactional
    public NoticiaDto editar(AuthUser admin, Long id, NoticiaRequest req) {
        Noticia n = obtener(id);
        boolean sePublica = !n.isPublicada() && req.publicada();
        aplicar(n, req);
        if (sePublica) {
            n.setFechaPublicacion(LocalDateTime.now());
        }
        actividad.registrar(admin, TipoActividad.NOTICIA, admin.nombre() + " editó la noticia «" + n.getTitulo() + "»");
        return NoticiaDto.of(n);
    }

    @Transactional
    public void eliminar(AuthUser admin, Long id) {
        Noticia n = obtener(id);
        noticias.delete(n);
        actividad.registrar(admin, TipoActividad.NOTICIA, admin.nombre() + " eliminó la noticia «" + n.getTitulo() + "»");
    }

    private static void aplicar(Noticia n, NoticiaRequest req) {
        n.setTitulo(req.titulo().trim());
        n.setResumen(AuthService.vacioANull(req.resumen()));
        n.setContenido(req.contenido());
        n.setImagenUrl(AuthService.vacioANull(req.imagenUrl()));
        n.setPublicada(req.publicada());
    }

    private Noticia obtener(Long id) {
        return noticias.findById(id).orElseThrow(() -> ApiException.noEncontrado("Noticia"));
    }

    private static int limitar(int tamano) {
        return Math.min(Math.max(1, tamano), 50);
    }
}
