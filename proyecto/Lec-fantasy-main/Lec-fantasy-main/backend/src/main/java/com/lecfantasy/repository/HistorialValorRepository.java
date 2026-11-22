package com.lecfantasy.repository;

import com.lecfantasy.domain.HistorialValor;
import com.lecfantasy.domain.Jornada;
import com.lecfantasy.domain.JugadorReal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistorialValorRepository extends JpaRepository<HistorialValor, Long> {

    @Query("select h from HistorialValor h left join fetch h.jornada where h.jugador.id = :jugadorId order by h.fecha asc, h.id asc")
    List<HistorialValor> findByJugador(@Param("jugadorId") Long jugadorId);

    boolean existsByJugadorId(Long jugadorId);

    /**
     * Apunta el nuevo valor de un jugador que acaba de cambiar de precio. La primera vez guarda
     * también el valor de partida, para que la gráfica empiece desde el precio original.
     */
    default void registrarCambio(JugadorReal jugador, Jornada jornada, LocalDateTime fecha) {
        if (jugador.getValorAnterior() != null && !existsByJugadorId(jugador.getId())) {
            save(new HistorialValor(jugador, null, jugador.getValorAnterior(), fecha.minusSeconds(1)));
        }
        save(new HistorialValor(jugador, jornada, jugador.getValorMercado(), fecha));
    }
}
