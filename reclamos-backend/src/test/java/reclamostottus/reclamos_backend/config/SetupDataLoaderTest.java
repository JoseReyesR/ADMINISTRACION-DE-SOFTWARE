package reclamostottus.reclamos_backend.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetupDataLoaderTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SetupDataLoader setupDataLoader;

    @Test
    void creaLosTresUsuariosDePruebaCuandoNingunoExiste() throws Exception {
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hash-encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        setupDataLoader.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(3)).save(captor.capture());

        assertThat(captor.getAllValues())
                .extracting(Usuario::getCorreo)
                .containsExactly("final@final", "test@admin", "cliente@tottus.com");
    }

    @Test
    void noCreaNingunUsuarioCuandoLosTresYaExisten() throws Exception {
        when(usuarioRepository.findByCorreo(eq("final@final"))).thenReturn(Optional.of(new Usuario()));
        when(usuarioRepository.findByCorreo(eq("test@admin"))).thenReturn(Optional.of(new Usuario()));
        when(usuarioRepository.findByCorreo(eq("cliente@tottus.com"))).thenReturn(Optional.of(new Usuario()));

        setupDataLoader.run();

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void creaSoloElUsuarioClienteCuandoLosOtrosDosYaExisten() throws Exception {
        when(usuarioRepository.findByCorreo(eq("final@final"))).thenReturn(Optional.of(new Usuario()));
        when(usuarioRepository.findByCorreo(eq("test@admin"))).thenReturn(Optional.of(new Usuario()));
        when(usuarioRepository.findByCorreo(eq("cliente@tottus.com"))).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hash-encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        setupDataLoader.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getCorreo()).isEqualTo("cliente@tottus.com");
    }
}
