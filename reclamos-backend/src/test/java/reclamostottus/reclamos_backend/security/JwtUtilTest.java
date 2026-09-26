package reclamostottus.reclamos_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    // Debe ser exactamente la misma clave secreta que usa JwtUtil internamente,
    // para poder construir tokens de prueba (uno expirado, otro con firma distinta).
    private static final String CLAVE_SECRETA = "TottusReclamosBackendSecretKey2026SeguridadJWT";
    private final Key secretKey = Keys.hmacShaKeyFor(CLAVE_SECRETA.getBytes());

    @Test
    void generarTokenYExtraerCorreoDevuelveElMismoCorreo() {
        String token = jwtUtil.generarToken("cliente@tottus.com", "ROLE_CLIENTE");

        String correoExtraido = jwtUtil.extraerCorreo(token);

        assertThat(correoExtraido).isEqualTo("cliente@tottus.com");
        assertThat(token).isNotBlank();
    }

    @Test
    void validarTokenDevuelveVerdaderoConCorreoCorrectoYTokenVigente() {
        String token = jwtUtil.generarToken("cliente@tottus.com", "ROLE_CLIENTE");

        Boolean esValido = jwtUtil.validarToken(token, "cliente@tottus.com");

        assertThat(esValido).isTrue();
    }

    @Test
    void validarTokenDevuelveFalsoCuandoElCorreoNoCoincide() {
        String token = jwtUtil.generarToken("cliente@tottus.com", "ROLE_CLIENTE");

        Boolean esValido = jwtUtil.validarToken(token, "otro@correo.com");

        assertThat(esValido).isFalse();
    }

    @Test
    void extraerClaimPermiteLeerUnClaimPersonalizado() {
        String token = jwtUtil.generarToken("admin@tottus.com", "ROLE_ADMIN");

        String rol = jwtUtil.extraerClaim(token, claims -> claims.get("rol", String.class));

        assertThat(rol).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void extraerCorreoLanzaExcepcionConTokenMalformado() {
        assertThatThrownBy(() -> jwtUtil.extraerCorreo("token-invalido-y-mal-formado"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validarTokenLanzaExcepcionConTokenYaExpirado() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ROLE_CLIENTE");

        // Construimos manualmente un token ya vencido, con la misma clave secreta
        // que usa JwtUtil, para simular el paso del tiempo.
        String tokenExpirado = Jwts.builder()
                .setClaims(claims)
                .setSubject("cliente@tottus.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 20_000))
                .setExpiration(new Date(System.currentTimeMillis() - 10_000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // La librería JJWT rechaza el token por vencido durante el parseo,
        // antes de que JwtUtil.validarToken llegue a evaluar la fecha manualmente.
        assertThatThrownBy(() -> jwtUtil.validarToken(tokenExpirado, "cliente@tottus.com"))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
