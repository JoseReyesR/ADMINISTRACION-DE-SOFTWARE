package reclamostottus.reclamos_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Map<String, Object> buscarPorDocumento(String numeroDocumento) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNumeroDocumento(numeroDocumento);
        Map<String, Object> respuesta = new HashMap<>();

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Verificamos si la cuenta está activa para evitar el autocompletado de cuentas
            // restringidas
            if (!usuario.getIsActive()) {
                throw new RuntimeException("Cuenta inactiva");
            }
            respuesta.put("encontrado", true);
            respuesta.put("nombres", usuario.getNombres());
            respuesta.put("apellidos", usuario.getApellidos());
            respuesta.put("correo", usuario.getCorreo());
            respuesta.put("telefono", usuario.getTelefono());
        } else {
            respuesta.put("encontrado", false);
        }
        return respuesta;
    }
}