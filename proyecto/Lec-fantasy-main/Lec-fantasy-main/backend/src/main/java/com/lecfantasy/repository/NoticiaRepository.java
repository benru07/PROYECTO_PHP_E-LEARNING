package com.lecfantasy.repository;

import com.lecfantasy.domain.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    Page<Noticia> findByPublicadaTrueOrderByFechaPublicacionDesc(Pageable pageable);

    Page<Noticia> findAllByOrderByFechaPublicacionDesc(Pageable pageable);
}
