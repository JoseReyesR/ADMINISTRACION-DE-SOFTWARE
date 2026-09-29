package reclamostottus.reclamos_backend.integration;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reclamostottus.reclamos_backend.dto.HistorialRequestDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba end-to-end del historial de seguimiento: registrar notas (internas y
 * públicas, con y sin cambio de estado) y verificar qué ve cada tipo de
 * lector (backoffice vs. público/invitado sin token).
 */
class HistorialSeguimientoIntegrationTest extends IntegrationTestSupport {

    @Test
    void unaNotaPublicaApareceEnElHistorialPublicoYEnElCompleto() throws Exception {
        ReclamoCreado reclamo = crearReclamoDePrueba("91000001", "hist-publico@correo.com");
        String token = obtenerToken("test@admin", "1234");

        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(reclamo.id());
        dto.setUsuarioResponsableId(1);
        dto.setComentario("Hemos revisado tu caso");
        dto.setEsInterno(false);

        mockMvc.perform(post("/api/historial")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value("Hemos revisado tu caso"));

        // Ruta pública: cualquiera puede leerla, sin token
        mockMvc.perform(get("/api/historial/" + reclamo.id() + "/publico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comentario").value("Hemos revisado tu caso"));

        // El backoffice también la ve dentro del historial completo
        mockMvc.perform(get("/api/historial/" + reclamo.id() + "/interno").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comentario").value("Hemos revisado tu caso"));
    }

    @Test
    void unaNotaInternaNoApareceEnElHistorialPublico() throws Exception {
        ReclamoCreado reclamo = crearReclamoDePrueba("91000002", "hist-interno@correo.com");
        String token = obtenerToken("test@admin", "1234");

        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(reclamo.id());
        dto.setUsuarioResponsableId(1);
        dto.setComentario("Nota interna de seguimiento");
        dto.setEsInterno(true);

        mockMvc.perform(post("/api/historial")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/historial/" + reclamo.id() + "/publico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/historial/" + reclamo.id() + "/interno").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void registrarHistorialConCambioDeEstadoActualizaElEstadoDelReclamo() throws Exception {
        ReclamoCreado reclamo = crearReclamoDePrueba("91000003", "hist-estado@correo.com");
        String token = obtenerToken("test@admin", "1234");

        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(reclamo.id());
        dto.setUsuarioResponsableId(1);
        dto.setComentario("Se actualizó el estado del caso");
        dto.setEsInterno(false);
        dto.setEstadoNuevoId(2);

        mockMvc.perform(post("/api/historial")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoNuevo.id").value(2))
                .andExpect(jsonPath("$.estadoAnterior.id").value(1));

        mockMvc.perform(get("/api/reclamos/admin/caso/" + reclamo.id()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado.id").value(2));
    }

    @Test
    void registrarHistorialSobreUnReclamoInexistenteDevuelve404() throws Exception {
        String token = obtenerToken("test@admin", "1234");

        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(999999);
        dto.setUsuarioResponsableId(1);
        dto.setComentario("No debería crearse");
        dto.setEsInterno(true);

        ServletException exception = assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/historial")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        });

        assertEquals("Reclamo no encontrado", exception.getCause().getMessage());
    }

    @Test
    void obtenerHistorialCompletoSinTokenDevuelve403() throws Exception {
        mockMvc.perform(get("/api/historial/1/interno"))
                .andExpect(status().isForbidden());
    }
}
