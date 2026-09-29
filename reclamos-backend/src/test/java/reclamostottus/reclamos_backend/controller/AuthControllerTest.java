package reclamostottus.reclamos_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reclamostottus.reclamos_backend.dto.LoginRequestDTO;
import reclamostottus.reclamos_backend.security.JwtUtil;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void loginConCredencialesCorrectasDevuelveToken() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setCorreo("cliente@tottus.com");
        request.setPassword("123456");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "cliente@tottus.com", "123456", List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generarToken(eq("cliente@tottus.com"), eq("ROLE_CLIENTE"))).thenReturn("token-jwt-generado");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-generado"));
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve401() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setCorreo("cliente@tottus.com");
        request.setPassword("mala-clave");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("mala clave"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas. Verifique la contraseña."));
    }

    @Test
    void loginConCuentaInactivaDevuelve401ConMensajeDelError() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setCorreo("cliente@tottus.com");
        request.setPassword("123456");

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("La cuenta está inactiva"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Acceso denegado: La cuenta está inactiva"));
    }
}
