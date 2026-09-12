package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reclamostottus.reclamos_backend.dto.LoginRequestDTO;
import reclamostottus.reclamos_backend.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        // 1. Spring Security valida el correo y la contraseña contra la Base de Datos
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));

        // 2. Extraemos el Rol que Spring Security cargó desde la base de datos
        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        // 3. Generamos el Token JWT inyectándole el correo y el rol
        String token = jwtUtil.generarToken(request.getCorreo(), rol);

        // 4. Devolvemos el token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}