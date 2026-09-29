package reclamostottus.reclamos_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import reclamostottus.reclamos_backend.dto.LoginRequestDTO;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base común para las pruebas de integración: levanta el contexto COMPLETO de
 * Spring Boot (controllers + servicios + seguridad + JPA) contra la H2 de
 * pruebas (src/test/resources/application.properties + data.sql), con el
 * SecurityFilterChain y el JwtRequestFilter reales activos detrás de MockMvc.
 *
 * @Transactional revierte los cambios de cada test al finalizar, así que un
 * reclamo/usuario creado en un test no contamina a los demás. Los 3 usuarios
 * de SetupDataLoader (final@final, test@admin, cliente@tottus.com) sí
 * persisten entre tests porque se insertan una sola vez, al arrancar el
 * contexto compartido (Spring lo cachea entre clases con la misma config).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    /** Pequeño resultado de crear un reclamo real vía HTTP, para encadenar el resto del flujo. */
    protected record ReclamoCreado(int id, String codigoSeguimiento) {
    }

    /** Hace login real contra /api/auth/login y devuelve el token JWT emitido. */
    protected String obtenerToken(String correo, String password) throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setCorreo(correo);
        login.setPassword(password);

        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(respuesta).get("token").asText();
    }

    /** Crea un reclamo real (sin archivo adjunto) vía POST /api/reclamos, para usarlo como fixture en otros flujos. */
    protected ReclamoCreado crearReclamoDePrueba(String numeroDocumento, String correo) throws Exception {
        ReclamoRequestDTO dto = new ReclamoRequestDTO();
        dto.setTipoDocumento("DNI");
        dto.setNumeroDocumento(numeroDocumento);
        dto.setNombres("Integración");
        dto.setApellidos("Prueba");
        dto.setCorreo(correo);
        dto.setCelular("999999999");
        dto.setTipoSolicitud("Reclamo");
        dto.setCanalCompra("Online");
        dto.setTienda("1");
        dto.setMotivo("1");
        dto.setProducto("Producto de prueba");
        dto.setDescripcion("Descripción generada por una prueba de integración");
        dto.setCategoria("1");

        MockMultipartFile reclamoPart = new MockMultipartFile("reclamo", "", "application/json",
                objectMapper.writeValueAsBytes(dto));

        String respuesta = mockMvc.perform(multipart("/api/reclamos").file(reclamoPart))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var json = objectMapper.readTree(respuesta);
        return new ReclamoCreado(json.get("id").asInt(), json.get("codigoSeguimiento").asText());
    }
}
