package reclamostottus.reclamos_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        if (!usuario.getIsActive()) {
            throw new RuntimeException("La cuenta del usuario está inactiva");
        }

        // --- INICIO DE RASTREO ---
        System.out.println("\n=== DEBUG DE AUTENTICACIÓN ===");
        System.out.println("1. Correo encontrado en BD: " + usuario.getCorreo());
        System.out.println("2. Hash exacto devuelto por MySQL: [" + usuario.getPassword() + "]");
        // --- FIN DE RASTREO ---

        String nombreRol = usuario.getRol().getNombre();
        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(nombreRol)));
    }
}