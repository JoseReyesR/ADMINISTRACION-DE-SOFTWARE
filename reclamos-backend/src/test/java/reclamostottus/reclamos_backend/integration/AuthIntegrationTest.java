package reclamostottus.reclamos_backend.integration;

import org.junit.jupiter.api.Test;
import reclamostottus.reclamos_backend.dto.LoginRequestDTO;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba end-to-end del login: request HTTP real -> AuthenticationManager real
 * -> UserDetailsServiceImpl real -> BCrypt real -> JwtUtil real, y verifica que
 * el token emitido efectivamente sirva para entrar a una ruta protegida.
 */
class AuthIntegrationTest extends IntegrationTestSupport {

    @Test
    void loginConCredencialesValidasDevuelveUnTokenQueFuncionaEnRutasProtegidas() throws Exception {
        // Usuario sembrado por SetupDataLoader al levantar el contexto
        String token = obtenerToken("cliente@tottus.com", "123456");

        mockMvc.perform(get("/api/reclamos/mis-casos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginDeUnaCuentaAdministradoraTambienDevuelveUnTokenUsable() throws Exception {
        String token = obtenerToken("test@admin", "1234");

        mockMvc.perform(get("/api/reclamos/admin/todos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginConPasswordIncorrectaDevuelve401() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setCorreo("cliente@tottus.com");
        login.setPassword("clave-incorrecta");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas. Verifique la contraseña."));
    }

    @Test
    void loginConCorreoInexistenteDevuelve401() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setCorreo("no-existe@tottus.com");
        login.setPassword("cualquiera");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }
}
