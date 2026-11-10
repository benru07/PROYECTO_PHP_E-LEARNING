package com.lecfantasy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Jwt jwt,
        Google google,
        Cors cors,
        String frontendUrl,
        Admin admin,
        Juego juego,
        Mail mail,
        Leaguepedia leaguepedia,
        Automatizacion automatizacion,
        PandaScore pandascore) {

    public record Jwt(String secret, long expiracionHoras, long expiracionRecordarmeDias) {
    }

    /** Client IDs de OAuth de Google (web y Android) que aceptamos como audiencia del ID token. */
    public record Google(List<String> clientIds) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    /** Si se definen, al arrancar se crea este administrador cuando no existe ninguno. */
    public record Admin(String email, String password, String nombre) {
    }

    public record Juego(BigDecimal valorInicialJugador, BigDecimal presupuestoLigaGlobal, int edadMinima) {
    }

    public record Mail(String from) {
    }

    public record Leaguepedia(String url, String userAgent) {
    }

    /**
     * Gestión automática de jornadas con datos de Leaguepedia.
     * habilitada: activa las tareas programadas (el botón "Ejecutar ahora" del admin funciona siempre).
     * cronInicio: cuándo se comprueba si hay que iniciar una jornada (cierra el mercado el día de inicio).
     * cronImportacion: cuándo se importan y cierran las jornadas ya terminadas (por defecto, los lunes).
     * finalizar: si es false solo importa y el admin revisa los puntos antes de cerrar a mano.
     */
    public record Automatizacion(boolean habilitada, String zona, String cronInicio, String cronImportacion,
                                 String cronCalendario, boolean finalizar) {
    }

    /**
     * API de PandaScore (calendario y resultados). token vacío = desactivado.
     * ligaId: id de la LEC en PandaScore; si no se indica se busca por ligaNombre.
     * diasCalendario: cuántos días hacia delante se miran para crear jornadas y mostrar partidos.
     */
    public record PandaScore(String url, String token, Long ligaId, String ligaNombre, int diasCalendario) {
    }
}
