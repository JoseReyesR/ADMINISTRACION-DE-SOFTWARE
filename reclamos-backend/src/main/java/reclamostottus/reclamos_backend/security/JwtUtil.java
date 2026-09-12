package reclamostottus.reclamos_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final String CLAVE_SECRETA = "TottusReclamosBackendSecretKey2026SeguridadJWT";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(CLAVE_SECRETA.getBytes());
    private final long TIEMPO_EXPIRACION = 1000 * 60 * 60 * 10;

    public String generarToken(String correo, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public Boolean validarToken(String token, String correo) {
        final String correoToken = extraerCorreo(token);
        return (correoToken.equals(correo) && !extraerClaim(token, Claims::getExpiration).before(new Date()));
    }
}