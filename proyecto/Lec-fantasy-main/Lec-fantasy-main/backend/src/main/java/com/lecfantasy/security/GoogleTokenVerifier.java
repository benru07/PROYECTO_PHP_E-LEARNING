package com.lecfantasy.security;

import com.lecfantasy.common.ApiException;
import com.lecfantasy.config.AppProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Verifica el ID token que devuelve "Continuar con Google" (Google Identity Services en la web
 * y Credential Manager en Android). Se comprueba firma, caducidad, emisor y audiencia.
 */
@Component
public class GoogleTokenVerifier {

    private static final String JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final Set<String> EMISORES = Set.of("accounts.google.com", "https://accounts.google.com");

    private final List<String> clientIds;
    private volatile JwtDecoder decoder;

    public GoogleTokenVerifier(AppProperties props) {
        List<String> ids = props.google() == null || props.google().clientIds() == null
                ? List.of() : props.google().clientIds();
        this.clientIds = ids.stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public GoogleUser verificar(String idToken) {
        if (clientIds.isEmpty()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "El inicio de sesión con Google no está configurado");
        }
        Jwt jwt;
        try {
            jwt = decoder().decode(idToken);
        } catch (JwtException e) {
            throw new BadCredentialsException("El token de Google no es válido");
        }
        Object verificado = jwt.getClaims().get("email_verified");
        if (!Boolean.TRUE.equals(verificado) && !"true".equals(String.valueOf(verificado))) {
            throw new BadCredentialsException("El email de Google no está verificado");
        }
        return new GoogleUser(jwt.getSubject(), jwt.getClaimAsString("email"), jwt.getClaimAsString("name"));
    }

    private JwtDecoder decoder() {
        if (decoder == null) {
            synchronized (this) {
                if (decoder == null) {
                    NimbusJwtDecoder d = NimbusJwtDecoder.withJwkSetUri(JWKS_URI).build();
                    OAuth2TokenValidator<Jwt> emisor = jwt -> EMISORES.contains(jwt.getClaimAsString("iss"))
                            ? OAuth2TokenValidatorResult.success()
                            : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Emisor no válido", null));
                    OAuth2TokenValidator<Jwt> audiencia = jwt -> jwt.getAudience() != null
                            && jwt.getAudience().stream().anyMatch(clientIds::contains)
                            ? OAuth2TokenValidatorResult.success()
                            : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Audiencia no válida", null));
                    d.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(), emisor, audiencia));
                    decoder = d;
                }
            }
        }
        return decoder;
    }

    public record GoogleUser(String googleId, String email, String nombre) {
    }
}
