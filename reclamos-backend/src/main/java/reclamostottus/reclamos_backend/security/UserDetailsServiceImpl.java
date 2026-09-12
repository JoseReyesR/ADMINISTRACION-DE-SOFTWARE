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
        // 1. Buscamos al usuario en la base de datos
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        // 2. Verificamos que la cuenta esté activa
        if (!usuario.getIsActive()) {
            throw new RuntimeException("La cuenta del usuario está inactiva");
        }

        // 3. Obtenemos el nombre del rol (Asumiendo que tu entidad Usuario tiene
        // relación con entidad Rol)
        // Nota: Spring Security espera que los roles tengan el prefijo "ROLE_" (ej.
        // "ROLE_ADMIN")
        String nombreRol = usuario.getRol().getNombre();

        // 4. Retornamos el objeto que Spring Security entiende para comparar las
        // contraseñas
        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getPassword(), // Aquí va el hash de BCrypt guardado en MySQL
                Collections.singletonList(new SimpleGrantedAuthority(nombreRol)));
    }
}