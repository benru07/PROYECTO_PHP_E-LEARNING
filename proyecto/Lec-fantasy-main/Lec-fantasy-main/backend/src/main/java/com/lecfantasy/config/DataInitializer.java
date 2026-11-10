package com.lecfantasy.config;

import com.lecfantasy.domain.Rol;
import com.lecfantasy.domain.Usuario;
import com.lecfantasy.repository.UsuarioRepository;
import com.lecfantasy.service.LigaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Al arrancar: crea la Liga Global si no existe y, si se han definido ADMIN_EMAIL y
 * ADMIN_PASSWORD y todavía no hay ningún administrador, crea el primero.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarios;
    private final LigaService ligaService;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties props;

    public DataInitializer(UsuarioRepository usuarios, LigaService ligaService, PasswordEncoder passwordEncoder,
                           AppProperties props) {
        this.usuarios = usuarios;
        this.ligaService = ligaService;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ligaService.ligaGlobal();
        AppProperties.Admin admin = props.admin();
        if (admin == null || isBlank(admin.email()) || isBlank(admin.password()) || usuarios.existsByRol(Rol.ADMIN)) {
            return;
        }
        Usuario u = usuarios.findByEmailIgnoreCase(admin.email()).orElseGet(Usuario::new);
        if (u.getId() == null) {
            u.setNombre(isBlank(admin.nombre()) ? "admin" : admin.nombre());
            u.setEmail(admin.email().toLowerCase(Locale.ROOT));
            u.setFechaRegistro(LocalDateTime.now());
        }
        u.setPasswordHash(passwordEncoder.encode(admin.password()));
        u.setRol(Rol.ADMIN);
        usuarios.save(u);
        ligaService.unirALigaGlobal(u);
        log.info("Administrador inicial creado: {}", u.getEmail());
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
