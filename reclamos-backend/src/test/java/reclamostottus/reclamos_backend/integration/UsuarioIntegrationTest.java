package reclamostottus.reclamos_backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.RolRepository;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba end-to-end de la búsqueda de cliente por número de documento
 * (usada por el frontend para autocompletar el formulario de reclamo).
 */
class UsuarioIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void buscarClientePorDocumentoDevuelveSusDatosCuandoExisteYEstaActivo() throws Exception {
        // Sembrado por SetupDataLoader al levantar el contexto (numeroDocumento 88889999)
        mockMvc.perform(get("/api/usuarios/documento/88889999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.encontrado").value(true))
                .andExpect(jsonPath("$.correo").value("cliente@tottus.com"));
    }

    @Test
    void buscarClientePorDocumentoDevuelve404CuandoNoExiste() throws Exception {
        mockMvc.perform(get("/api/usuarios/documento/00000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarClientePorDocumentoDevuelve403CuandoLaCuentaEstaInactiva() throws Exception {
        Usuario inactivo = new Usuario();
        inactivo.setTipoDocumento("DNI");
        inactivo.setNumeroDocumento("92000001");
        inactivo.setNombres("Inactivo");
        inactivo.setApellidos("Prueba");
        inactivo.setCorreo("inactivo@correo.com");
        inactivo.setPassword(passwordEncoder.encode("123456"));
        inactivo.setRol(rolRepository.findById(1).orElseThrow());
        inactivo.setIsActive(false);
        usuarioRepository.save(inactivo);

        mockMvc.perform(get("/api/usuarios/documento/92000001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Cuenta inactiva"));
    }
}
