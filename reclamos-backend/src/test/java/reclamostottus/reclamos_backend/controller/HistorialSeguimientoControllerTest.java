package reclamostottus.reclamos_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reclamostottus.reclamos_backend.dto.HistorialRequestDTO;
import reclamostottus.reclamos_backend.model.HistorialSeguimiento;
import reclamostottus.reclamos_backend.service.HistorialSeguimientoService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HistorialSeguimientoControllerTest {

    @Mock
    private HistorialSeguimientoService historialService;

    @InjectMocks
    private HistorialSeguimientoController historialSeguimientoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(historialSeguimientoController).build();
    }

    @Test
    void registrarDevuelveElHistorialCreado() throws Exception {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(1);
        dto.setComentario("Nota de prueba");
        dto.setEsInterno(true);

        HistorialSeguimiento creado = new HistorialSeguimiento();
        creado.setId(1);
        creado.setComentario("Nota de prueba");
        when(historialService.registrarHistorial(any(HistorialRequestDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/historial")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value("Nota de prueba"));
    }

    @Test
    void obtenerCompletoDevuelveElHistorialCompletoDelReclamo() throws Exception {
        HistorialSeguimiento historial = new HistorialSeguimiento();
        historial.setId(1);
        historial.setEsInterno(true);
        when(historialService.obtenerHistorialCompleto(5)).thenReturn(List.of(historial));

        mockMvc.perform(get("/api/historial/5/interno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].esInterno").value(true));
    }

    @Test
    void obtenerPublicoDevuelveSoloElHistorialPublicoDelReclamo() throws Exception {
        HistorialSeguimiento historial = new HistorialSeguimiento();
        historial.setId(2);
        historial.setEsInterno(false);
        when(historialService.obtenerHistorialPublico(5)).thenReturn(List.of(historial));

        mockMvc.perform(get("/api/historial/5/publico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].esInterno").value(false));
    }
}
