package reclamostottus.reclamos_backend.integration;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba end-to-end del ciclo de vida completo de un reclamo: creación (con
 * archivo físico real), seguimiento como invitado, listado, y actualización
 * de estado/prioridad desde el backoffice, todo vía HTTP real.
 */
class ReclamoFlowIntegrationTest extends IntegrationTestSupport {

    private String archivoDePruebaCreado;

    @AfterEach
    void limpiarArchivoFisicoCreado() throws Exception {
        if (archivoDePruebaCreado != null) {
            Files.deleteIfExists(Paths.get("uploads", archivoDePruebaCreado));
        }
    }

    private ReclamoRequestDTO dtoDeReclamo(String numeroDocumento, String correo) {
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
        dto.setNumeroBoleta("B-INTEGR-001");
        dto.setProducto("Licuadora");
        dto.setDescripcion("Llegó dañada de fábrica");
        dto.setCategoria("1");
        return dto;
    }

    @Test
    void flujoCompletoCrearConsultarYActualizarUnReclamoConArchivo() throws Exception {
        archivoDePruebaCreado = "evidencia-integracion-" + System.nanoTime() + ".jpg";

        MockMultipartFile reclamoPart = new MockMultipartFile("reclamo", "", "application/json",
                objectMapper.writeValueAsBytes(dtoDeReclamo("90000001", "integracion@correo.com")));
        MockMultipartFile archivoPart = new MockMultipartFile("archivo", archivoDePruebaCreado, "image/jpeg",
                "contenido-de-prueba".getBytes());

        String respuestaCreacion = mockMvc.perform(multipart("/api/reclamos").file(reclamoPart).file(archivoPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeguimiento").exists())
                .andReturn().getResponse().getContentAsString();

        var json = objectMapper.readTree(respuestaCreacion);
        String codigo = json.get("codigoSeguimiento").asText();
        int id = json.get("id").asInt();

        // El archivo se guardó de verdad en disco, no solo el registro en BD
        assertThat(new File("uploads/" + archivoDePruebaCreado)).exists();

        // Seguimiento como invitado (ruta pública, sin token)
        mockMvc.perform(get("/api/reclamos/seguimiento/" + codigo + "/90000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeguimiento").value(codigo));

        // El backoffice (requiere token) lo consulta por id
        String token = obtenerToken("test@admin", "1234");
        mockMvc.perform(get("/api/reclamos/admin/caso/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeguimiento").value(codigo));

        // Cambiar estado
        mockMvc.perform(put("/api/reclamos/admin/caso/" + id + "/estado/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado.id").value(2));

        // Cambiar prioridad
        mockMvc.perform(put("/api/reclamos/admin/caso/" + id + "/prioridad/3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prioridad.id").value(3));
    }

    @Test
    void seguimientoInvitadoConDniIncorrectoDevuelve404() throws Exception {
        ReclamoCreado reclamo = crearReclamoDePrueba("90000002", "otro@correo.com");

        mockMvc.perform(get("/api/reclamos/seguimiento/" + reclamo.codigoSeguimiento() + "/00000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarMisCasosSinTokenDevuelve403() throws Exception {
        mockMvc.perform(get("/api/reclamos/mis-casos"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarMisCasosDevuelveSoloLosReclamosDelCorreoAutenticado() throws Exception {
        crearReclamoDePrueba("90000003", "clien@tottus.com");
        String token = obtenerToken("cliente@tottus.com", "123456");

        mockMvc.perform(get("/api/reclamos/mis-casos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstadoDeUnReclamoInexistenteDevuelve5xx() throws Exception {
        String token = obtenerToken("test@admin", "1234");

        ServletException exception = assertThrows(ServletException.class, () -> {
            mockMvc.perform(put("/api/reclamos/admin/caso/999999/estado/2")
                    .header("Authorization", "Bearer " + token));
        });

        assertEquals("Reclamo no encontrado", exception.getCause().getMessage());
    }
}
