package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
        try {

            // --- INICIO DE RASTREO ---
            System.out.println("3. Contraseña recibida de Postman: [" + request.getPassword() + "]");
            System.out.println("==============================\n");
            // --- FIN DE RASTREO ---

            // 1. Spring Security valida el correo y la contraseña contra la BD
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));

            // 2. Si pasa, generamos el Token JWT
            String rol = authentication.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generarToken(request.getCorreo(), rol);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // 3. Atrapamos el error de contraseña para evitar el 403 en Postman
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales incorrectas. Verifique la contraseña.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            // 4. Atrapamos errores de cuenta inactiva o usuario inexistente
            Map<String, String> error = new HashMap<>();
            error.put("error", "Acceso denegado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}