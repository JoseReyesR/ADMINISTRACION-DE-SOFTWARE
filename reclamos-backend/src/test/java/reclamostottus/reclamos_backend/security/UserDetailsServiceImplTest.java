package reclamostottus.reclamos_backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reclamostottus.reclamos_backend.model.Rol;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setId(2);
        rol.setNombre("ROLE_ADMIN");

        usuarioActivo = new Usuario();
        usuarioActivo.setCorreo("admin@tottus.com");
        usuarioActivo.setPassword("hash-bcrypt");
        usuarioActivo.setRol(rol);
        usuarioActivo.setIsActive(true);
    }

    @Test
    void loadUserByUsernameDevuelveUserDetailsCuandoElUsuarioEstaActivo() {
        when(usuarioRepository.findByCorreo(eq("admin@tottus.com"))).thenReturn(Optional.of(usuarioActivo));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@tottus.com");

        assertThat(userDetails.getUsername()).isEqualTo("admin@tottus.com");
        assertThat(userDetails.getPassword()).isEqualTo("hash-bcrypt");
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsernameLanzaExcepcionCuandoLaCuentaEstaInactiva() {
        usuarioActivo.setIsActive(false);
        when(usuarioRepository.findByCorreo(eq("admin@tottus.com"))).thenReturn(Optional.of(usuarioActivo));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("admin@tottus.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("inactiva");
    }

    @Test
    void loadUserByUsernameLanzaExcepcionCuandoElUsuarioNoExiste() {
        when(usuarioRepository.findByCorreo(eq("no-existe@tottus.com"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("no-existe@tottus.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
