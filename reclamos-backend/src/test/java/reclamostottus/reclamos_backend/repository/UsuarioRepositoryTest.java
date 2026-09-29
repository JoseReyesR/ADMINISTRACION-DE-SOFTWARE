package reclamostottus.reclamos_backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import reclamostottus.reclamos_backend.model.Rol;
import reclamostottus.reclamos_backend.model.Usuario;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        rolCliente = new Rol();
        rolCliente.setNombre("ROLE_CLIENTE_" + System.nanoTime());
        entityManager.persistAndFlush(rolCliente);
    }

    private Usuario nuevoUsuario(String correo, String numeroDocumento) {
        Usuario usuario = new Usuario();
        usuario.setTipoDocumento("DNI");
        usuario.setNumeroDocumento(numeroDocumento);
        usuario.setNombres("Nombre");
        usuario.setApellidos("Apellido");
        usuario.setCorreo(correo);
        usuario.setTelefono("999999999");
        usuario.setPassword("hash");
        usuario.setRol(rolCliente);
        usuario.setIsActive(true);
        return usuario;
    }

    @Test
    void findByCorreoDevuelveElUsuarioCuandoExiste() {
        entityManager.persistAndFlush(nuevoUsuario("existente@correo.com", "10000001"));

        Optional<Usuario> resultado = usuarioRepository.findByCorreo("existente@correo.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNumeroDocumento()).isEqualTo("10000001");
    }

    @Test
    void findByCorreoDevuelveVacioCuandoNoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findByCorreo("no-existe@correo.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByNumeroDocumentoDevuelveElUsuarioCuandoExiste() {
        entityManager.persistAndFlush(nuevoUsuario("otro@correo.com", "20000002"));

        Optional<Usuario> resultado = usuarioRepository.findByNumeroDocumento("20000002");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCorreo()).isEqualTo("otro@correo.com");
    }

    @Test
    void findByNumeroDocumentoDevuelveVacioCuandoNoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findByNumeroDocumento("99999999");

        assertThat(resultado).isEmpty();
    }

    @Test
    void saveYFindByIdPersistenElUsuario() {
        Usuario guardado = usuarioRepository.save(nuevoUsuario("save@correo.com", "30000003"));

        Optional<Usuario> encontrado = usuarioRepository.findById(guardado.getId());

        // fecha_registro es insertable=false/updatable=false (la asigna la BD real vía
        // DEFAULT/trigger de MySQL); en H2 de pruebas puede quedar en null, así que
        // solo verificamos que el registro se haya guardado correctamente.
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCorreo()).isEqualTo("save@correo.com");
    }
}
