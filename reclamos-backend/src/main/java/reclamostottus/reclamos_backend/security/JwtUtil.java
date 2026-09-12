package reclamostottus.reclamos_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long TIEMPO_EXPIRACION = 1000 * 60 * 60 * 10;

    public String generarToken(String correo, String rol) {
        // Agregamos el rol al "cuerpo" del token
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", rol);

        return Jwts.builder()
                .setClaims(claims) // Inyectamos los datos extra
                .setSubject(correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    public Boolean validarToken(String token, String correo) {
        final String correoToken = extraerCorreo(token);
        return (correoToken.equals(correo) && !esTokenExpirado(token));
    }

    private Boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }
}