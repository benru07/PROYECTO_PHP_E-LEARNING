package com.lecfantasy.web;

import com.lecfantasy.dto.AuthDtos.*;
import com.lecfantasy.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
        return auth.registrar(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }

    @PostMapping("/google")
    public AuthResponse google(@Valid @RequestBody GoogleLoginRequest req) {
        return auth.loginGoogle(req);
    }

    @PostMapping("/olvido-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void olvido(@Valid @RequestBody OlvidoPasswordRequest req) {
        auth.olvidoPassword(req);
    }

    @PostMapping("/restablecer-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecer(@Valid @RequestBody RestablecerPasswordRequest req) {
        auth.restablecerPassword(req);
    }
}
