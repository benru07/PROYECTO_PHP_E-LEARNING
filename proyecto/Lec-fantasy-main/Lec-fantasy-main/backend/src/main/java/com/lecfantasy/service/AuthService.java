package com.lecfantasy.service;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.config.AppProperties;
import com.lecfantasy.domain.Baneo;
import com.lecfantasy.domain.EquipoPro;
import com.lecfantasy.domain.PasswordResetToken;
import com.lecfantasy.domain.Rol;
import com.lecfantasy.domain.Usuario;
import com.lecfantasy.dto.AuthDtos.*;
import com.lecfantasy.dto.UsuarioDtos;
import com.lecfantasy.repository.BaneoRepository;
import com.lecfantasy.repository.EquipoProRepository;
import com.lecfantasy.repository.PasswordResetTokenRepository;
import com.lecfantasy.repository.UsuarioRepository;
import com.lecfantasy.security.GoogleTokenVerifier;
import com.lecfantasy.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
public class AuthService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UsuarioRepository usuarios;
    private final EquipoProRepository equiposPro;
    private final BaneoRepository baneos;
    private final PasswordResetTokenRepository resetTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleVerifier;
    private final LigaService ligaService;
    private final MailService mailService;
    private final AppProperties props;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UsuarioRepository usuarios, EquipoProRepository equiposPro, BaneoRepository baneos,
                       PasswordResetTokenRepository resetTokens, PasswordEncoder passwordEncoder,
                       JwtService jwtService, GoogleTokenVerifier googleVerifier, LigaService ligaService,
                       MailService mailService, AppProperties props) {
        this.usuarios = usuarios;
        this.equiposPro = equiposPro;
        this.baneos = baneos;
        this.resetTokens = resetTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleVerifier = googleVerifier;
        this.ligaService = ligaService;
        this.mailService = mailService;
        this.props = props;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest req) {
        if (!req.password().equals(req.repetirPassword())) {
            throw ApiException.peticionInvalida("Las contraseñas no coinciden");
        }
        if (!req.aceptaTerminos()) {
            throw ApiException.peticionInvalida("Debes aceptar los Términos de uso y la Política de privacidad");
        }
        validarEdad(req.fechaNacimiento());
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByNombreIgnoreCase(req.nombre())) {
            throw ApiException.conflicto("Ese nombre de usuario ya está en uso");
        }
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflicto("Ya existe una cuenta con ese correo");
        }
        Usuario u = new Usuario();
        u.setNombre(req.nombre());
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRol(Rol.USER);
        u.setFechaNacimiento(req.fechaNacimiento());
        u.setPais(req.pais().toUpperCase(Locale.ROOT));
        u.setEquipoFavorito(buscarEquipoFavorito(req.equipoFavoritoId()));
        u.setRiotId(vacioANull(req.riotId()));
        u.setAceptaNoticias(req.aceptaNoticias());
        u.setFechaRegistro(LocalDateTime.now());
        u.setUltimoAcceso(LocalDateTime.now());
        usuarios.save(u);
        ligaService.unirALigaGlobal(u);
        return respuesta(u, false);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        String id = req.identificador().trim();
        Usuario u = (id.contains("@") ? usuarios.findByEmailIgnoreCase(id) : usuarios.findByNombreIgnoreCase(id))
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));
        if (u.getPasswordHash() == null) {
            throw new BadCredentialsException("Esta cuenta se creó con Google. Usa «Continuar con Google».");
        }
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        comprobarBaneo(u);
        u.setUltimoAcceso(LocalDateTime.now());
        return respuesta(u, req.recordarme());
    }

    @Transactional
    public AuthResponse loginGoogle(GoogleLoginRequest req) {
        GoogleTokenVerifier.GoogleUser g = googleVerifier.verificar(req.idToken());
        String email = g.email().toLowerCase(Locale.ROOT);
        Usuario u = usuarios.findByGoogleId(g.googleId())
                .or(() -> usuarios.findByEmailIgnoreCase(email))
                .orElse(null);
        if (u == null) {
            u = new Usuario();
            u.setNombre(nombreDisponible(g.nombre(), email));
            u.setEmail(email);
            u.setRol(Rol.USER);
            u.setFechaRegistro(LocalDateTime.now());
            u.setGoogleId(g.googleId());
            u.setUltimoAcceso(LocalDateTime.now());
            usuarios.save(u);
            ligaService.unirALigaGlobal(u);
        } else {
            if (u.getGoogleId() == null) {
                u.setGoogleId(g.googleId()); // vincula la cuenta existente con Google
            }
            comprobarBaneo(u);
            u.setUltimoAcceso(LocalDateTime.now());
        }
        return respuesta(u, true);
    }

    /** Siempre responde igual, exista o no el correo, para no revelar qué cuentas existen. */
    @Transactional
    public void olvidoPassword(OlvidoPasswordRequest req) {
        usuarios.findByEmailIgnoreCase(req.email().trim()).ifPresent(u -> {
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            PasswordResetToken t = new PasswordResetToken();
            t.setUsuario(u);
            t.setTokenHash(sha256(token));
            t.setExpira(LocalDateTime.now().plusHours(ReglasJuego.HORAS_VALIDEZ_RESET));
            resetTokens.save(t);
            String enlace = props.frontendUrl() + "/restablecer-password?token=" + token;
            mailService.enviar(u.getEmail(), "LEC Fantasy · Restablecer contraseña",
                    "Hola " + u.getNombre() + ",\n\nPara elegir una contraseña nueva entra en:\n" + enlace
                            + "\n\nEl enlace caduca en " + ReglasJuego.HORAS_VALIDEZ_RESET
                            + " hora. Si no lo has pedido tú, ignora este correo.");
        });
    }

    @Transactional
    public void restablecerPassword(RestablecerPasswordRequest req) {
        PasswordResetToken t = resetTokens.findByTokenHash(sha256(req.token()))
                .orElseThrow(() -> ApiException.peticionInvalida("El enlace no es válido"));
        if (t.isUsado() || t.getExpira().isBefore(LocalDateTime.now())) {
            throw ApiException.peticionInvalida("El enlace ha caducado. Pide uno nuevo.");
        }
        t.setUsado(true);
        t.getUsuario().setPasswordHash(passwordEncoder.encode(req.password()));
    }

    // ------------------------------------------------------------------

    private AuthResponse respuesta(Usuario u, boolean recordarme) {
        JwtService.TokenEmitido token = jwtService.emitir(u, recordarme);
        return new AuthResponse(token.token(), token.expira(), UsuarioDtos.UsuarioPrivado.of(u));
    }

    private void comprobarBaneo(Usuario u) {
        List<Baneo> activos = baneos.findActivos(u.getId(), LocalDateTime.now());
        if (activos.isEmpty()) {
            return;
        }
        boolean permanente = activos.stream().anyMatch(Baneo::isPermanente);
        if (permanente) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido baneada de forma permanente");
        }
        LocalDateTime hasta = activos.stream().map(Baneo::getFin).max(LocalDateTime::compareTo).orElseThrow();
        throw new ApiException(HttpStatus.FORBIDDEN, "Tu cuenta está baneada hasta el " + FORMATO_FECHA.format(hasta) + " (UTC)");
    }

    void validarEdad(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < props.juego().edadMinima()) {
            throw ApiException.peticionInvalida("Debes tener al menos " + props.juego().edadMinima() + " años");
        }
    }

    EquipoPro buscarEquipoFavorito(Long id) {
        if (id == null) {
            return null;
        }
        return equiposPro.findById(id).orElseThrow(() -> ApiException.peticionInvalida("Equipo favorito no válido"));
    }

    private String nombreDisponible(String nombreGoogle, String email) {
        String base = (nombreGoogle != null && !nombreGoogle.isBlank() ? nombreGoogle : email.substring(0, email.indexOf('@')))
                .replaceAll("[^A-Za-z0-9_]", "");
        if (base.length() < 3) {
            base = "jugador" + base;
        }
        if (base.length() > 16) {
            base = base.substring(0, 16);
        }
        String candidato = base;
        int intento = 0;
        while (usuarios.existsByNombreIgnoreCase(candidato)) {
            candidato = base + (100 + random.nextInt(9900));
            if (++intento > 20) {
                candidato = "jugador" + System.currentTimeMillis() % 1_000_000_000L;
            }
        }
        return candidato;
    }

    static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    static String sha256(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
