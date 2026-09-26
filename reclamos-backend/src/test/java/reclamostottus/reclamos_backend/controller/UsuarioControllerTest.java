package reclamostottus.reclamos_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reclamostottus.reclamos_backend.service.UsuarioService;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void buscarClientePorDocumentoDevuelve200CuandoElClienteExiste() throws Exception {
        Map<String, Object> respuesta = Map.of(
                "encontrado", true,
                "nombres", "Ana",
                "apellidos", "Gomez",
                "correo", "ana@correo.com",
                "telefono", "999888777");
        when(usuarioService.buscarPorDocumento("12345678")).thenReturn(respuesta);

        mockMvc.perform(get("/api/usuarios/documento/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.encontrado").value(true))
                .andExpect(jsonPath("$.nombres").value("Ana"));
    }

    @Test
    void buscarClientePorDocumentoDevuelve404CuandoNoExiste() throws Exception {
        when(usuarioService.buscarPorDocumento("00000000")).thenReturn(Map.of("encontrado", false));

        mockMvc.perform(get("/api/usuarios/documento/00000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarClientePorDocumentoDevuelve403CuandoLaCuentaEstaInactiva() throws Exception {
        when(usuarioService.buscarPorDocumento("12345678"))
                .thenThrow(new RuntimeException("Cuenta inactiva"));

        mockMvc.perform(get("/api/usuarios/documento/12345678"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Cuenta inactiva"));
    }
}
