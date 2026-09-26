package reclamostottus.reclamos_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void buscarPorDocumentoDevuelveDatosCuandoElUsuarioExisteYEstaActivo() {
        Usuario usuario = new Usuario();
        usuario.setNombres("Ana");
        usuario.setApellidos("Gomez");
        usuario.setCorreo("ana@correo.com");
        usuario.setTelefono("999888777");
        usuario.setIsActive(true);

        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuario));

        Map<String, Object> respuesta = usuarioService.buscarPorDocumento("12345678");

        assertThat(respuesta.get("encontrado")).isEqualTo(true);
        assertThat(respuesta.get("nombres")).isEqualTo("Ana");
        assertThat(respuesta.get("apellidos")).isEqualTo("Gomez");
        assertThat(respuesta.get("correo")).isEqualTo("ana@correo.com");
        assertThat(respuesta.get("telefono")).isEqualTo("999888777");
    }

    @Test
    void buscarPorDocumentoDevuelveEncontradoFalsoCuandoNoExiste() {
        when(usuarioRepository.findByNumeroDocumento("00000000")).thenReturn(Optional.empty());

        Map<String, Object> respuesta = usuarioService.buscarPorDocumento("00000000");

        assertThat(respuesta.get("encontrado")).isEqualTo(false);
        assertThat(respuesta).doesNotContainKey("nombres");
    }

    @Test
    void buscarPorDocumentoLanzaExcepcionCuandoLaCuentaEstaInactiva() {
        Usuario usuario = new Usuario();
        usuario.setIsActive(false);

        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.buscarPorDocumento("12345678"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta inactiva");
    }
}
