package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reclamostottus.reclamos_backend.service.UsuarioService;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/documento/{numero}")
    public ResponseEntity<Map<String, Object>> buscarClientePorDocumento(@PathVariable String numero) {
        try {
            Map<String, Object> resultado = usuarioService.buscarPorDocumento(numero);
            if ((Boolean) resultado.get("encontrado")) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.notFound().build(); // Devuelve error 404 para indicar a Angular que es invitado
            }
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage())); // Devuelve 403 si está inactivo
        }
    }
}