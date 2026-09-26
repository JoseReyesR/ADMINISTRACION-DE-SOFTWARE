package reclamostottus.reclamos_backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reclamostottus.reclamos_backend.security.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba end-to-end del control de acceso definido en SecurityConfig: qué
 * rutas son públicas, cuáles exigen un token válido, y cómo responde el
 * JwtRequestFilter real ante tokens ausentes, malformados o simplemente
 * válidos (nótese que este filtro no verifica que el usuario del token
 * exista en BD; solo valida la firma/vigencia del JWT).
 */
class SecurityAccessIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void rutasPublicasSonAccesiblesSinToken() throws Exception {
        mockMvc.perform(get("/api/catalogos/tiendas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/catalogos/motivos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/catalogos/categorias")).andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios/documento/00000000")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/reclamos/seguimiento/NO-EXISTE/00000000")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/historial/1/publico")).andExpect(status().isOk());
        // /uploads/** es pública: sin token da 404 (no existe el archivo), nunca 401/403
        mockMvc.perform(get("/uploads/archivo-que-no-existe.jpg")).andExpect(status().isNotFound());
    }

    @Test
    void rutasProtegidasSinTokenDevuelven403() throws Exception {
        mockMvc.perform(get("/api/reclamos/mis-casos")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reclamos/admin/todos")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reclamos/admin/estados")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reclamos/admin/prioridades")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reclamos/todos")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/historial/1/interno")).andExpect(status().isForbidden());
    }

    @Test
    void rutasProtegidasConTokenValidoDevuelven200() throws Exception {
        String token = obtenerToken("cliente@tottus.com", "123456");

        mockMvc.perform(get("/api/reclamos/mis-casos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/reclamos/admin/todos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/reclamos/todos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void rutaProtegidaConTokenMalformadoDevuelve403() throws Exception {
        mockMvc.perform(get("/api/reclamos/mis-casos").header("Authorization", "Bearer token-malformado"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rutaProtegidaSinElPrefijoBearerDevuelve403() throws Exception {
        String token = obtenerToken("cliente@tottus.com", "123456");

        mockMvc.perform(get("/api/reclamos/mis-casos").header("Authorization", token)) // sin "Bearer "
                .andExpect(status().isForbidden());
    }

    @Test
    void unTokenValidoParaUnCorreoQueNoExisteEnBdIgualmenteAutentica() throws Exception {
        // Documenta el comportamiento real: el filtro JWT confía en la firma/vigencia
        // del token y NO vuelve a verificar contra la BD que el usuario exista.
        String token = jwtUtil.generarToken("fantasma@correo.com", "ROLE_CLIENTE");

        mockMvc.perform(get("/api/reclamos/mis-casos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
