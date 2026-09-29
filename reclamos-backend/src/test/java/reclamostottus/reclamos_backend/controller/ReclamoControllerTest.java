package reclamostottus.reclamos_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.EstadoReclamo;
import reclamostottus.reclamos_backend.model.Prioridad;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.EstadoReclamoRepository;
import reclamostottus.reclamos_backend.repository.PrioridadRepository;
import reclamostottus.reclamos_backend.repository.ReclamoRepository;
import reclamostottus.reclamos_backend.service.ReclamoService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReclamoControllerTest {

    @Mock
    private ReclamoService reclamoService;

    @Mock
    private EstadoReclamoRepository estadoRepository;

    @Mock
    private PrioridadRepository prioridadRepository;

    @Mock
    private ReclamoRepository reclamoRepository;

    @InjectMocks
    private ReclamoController reclamoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reclamoController).build();
    }

    @Test
    void crearReclamoConArchivoDevuelveElReclamoCreado() throws Exception {
        ReclamoRequestDTO dto = new ReclamoRequestDTO();
        dto.setTipoDocumento("DNI");
        dto.setNumeroDocumento("12345678");
        dto.setNombres("Cliente");
        dto.setApellidos("Prueba");
        dto.setCorreo("cliente@correo.com");
        dto.setMotivo("1");
        dto.setDescripcion("Descripción del caso");

        Reclamo reclamoCreado = new Reclamo();
        reclamoCreado.setId(1);
        reclamoCreado.setCodigoSeguimiento("REQ-2026-0001");
        when(reclamoService.registrarReclamo(any(ReclamoRequestDTO.class), any())).thenReturn(reclamoCreado);

        var reclamoPart = new org.springframework.mock.web.MockMultipartFile(
                "reclamo", "", "application/json", objectMapper.writeValueAsBytes(dto));
        var archivoPart = new org.springframework.mock.web.MockMultipartFile(
                "archivo", "foto.jpg", "image/jpeg", "contenido".getBytes());

        mockMvc.perform(multipart("/api/reclamos").file(reclamoPart).file(archivoPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeguimiento").value("REQ-2026-0001"));
    }

    @Test
    void crearReclamoSinArchivoDevuelveElReclamoCreado() throws Exception {
        ReclamoRequestDTO dto = new ReclamoRequestDTO();
        dto.setNumeroDocumento("12345678");
        dto.setMotivo("1");
        dto.setDescripcion("Descripción del caso");

        Reclamo reclamoCreado = new Reclamo();
        reclamoCreado.setId(2);
        reclamoCreado.setCodigoSeguimiento("REQ-2026-0002");
        when(reclamoService.registrarReclamo(any(ReclamoRequestDTO.class), isNull())).thenReturn(reclamoCreado);

        var reclamoPart = new org.springframework.mock.web.MockMultipartFile(
                "reclamo", "", "application/json", objectMapper.writeValueAsBytes(dto));

        mockMvc.perform(multipart("/api/reclamos").file(reclamoPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeguimiento").value("REQ-2026-0002"));
    }

    @Test
    void listarReclamosDevuelveLaListaCompleta() throws Exception {
        when(reclamoService.listarTodos()).thenReturn(List.of(new Reclamo()));

        mockMvc.perform(get("/api/reclamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void seguimientoInvitadoDevuelveElReclamoCuandoExiste() throws Exception {
        Reclamo reclamo = new Reclamo();
        reclamo.setCodigoSeguimiento("REQ-2026-0003");
        when(reclamoService.seguimientoSeguroInvitado("REQ-2026-0003", "12345678"))
                .thenReturn(Optional.of(reclamo));

        mockMvc.perform(get("/api/reclamos/seguimiento/REQ-2026-0003/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeguimiento").value("REQ-2026-0003"));
    }

    @Test
    void seguimientoInvitadoDevuelve404CuandoNoExiste() throws Exception {
        when(reclamoService.seguimientoSeguroInvitado("NO-EXISTE", "00000000"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reclamos/seguimiento/NO-EXISTE/00000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarMisCasosUsaElCorreoDelTokenAutenticado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setCorreo("cliente@correo.com");
        Reclamo reclamo = new Reclamo();
        reclamo.setUsuario(usuario);
        when(reclamoService.obtenerMisCasos("cliente@correo.com")).thenReturn(List.of(reclamo));

        RequestPostProcessor autenticadoComo = request -> {
            request.setUserPrincipal(new UsernamePasswordAuthenticationToken("cliente@correo.com", null));
            return request;
        };

        mockMvc.perform(get("/api/reclamos/mis-casos").with(autenticadoComo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listarCasosAdministrativosDevuelveTodosLosCasos() throws Exception {
        when(reclamoService.listarTodos()).thenReturn(List.of(new Reclamo(), new Reclamo()));

        mockMvc.perform(get("/api/reclamos/admin/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void obtenerCasoAdminDevuelveElCasoCuandoExiste() throws Exception {
        Reclamo reclamo = new Reclamo();
        reclamo.setId(7);
        when(reclamoRepository.findById(7)).thenReturn(Optional.of(reclamo));

        mockMvc.perform(get("/api/reclamos/admin/caso/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void obtenerCasoAdminDevuelve404CuandoNoExiste() throws Exception {
        when(reclamoRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reclamos/admin/caso/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cambiarEstadoDevuelveElReclamoActualizado() throws Exception {
        Reclamo reclamo = new Reclamo();
        reclamo.setId(1);
        when(reclamoService.actualizarEstadoReclamo(1, 2)).thenReturn(reclamo);

        mockMvc.perform(put("/api/reclamos/admin/caso/1/estado/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void cambiarPrioridadDevuelveElReclamoActualizado() throws Exception {
        Reclamo reclamo = new Reclamo();
        reclamo.setId(1);
        when(reclamoService.actualizarPrioridadReclamo(1, 3)).thenReturn(reclamo);

        mockMvc.perform(put("/api/reclamos/admin/caso/1/prioridad/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listarEstadosDevuelveElCatalogoDeEstados() throws Exception {
        EstadoReclamo estado = new EstadoReclamo();
        estado.setNombre("Registrado");
        when(estadoRepository.findAll()).thenReturn(List.of(estado));

        mockMvc.perform(get("/api/reclamos/admin/estados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Registrado"));
    }

    @Test
    void listarPrioridadesDevuelveElCatalogoDePrioridades() throws Exception {
        Prioridad prioridad = new Prioridad();
        prioridad.setNombre("Alta");
        when(prioridadRepository.findAll()).thenReturn(List.of(prioridad));

        mockMvc.perform(get("/api/reclamos/admin/prioridades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Alta"));
    }

    @Test
    void listarTodosDevuelveTodosLosReclamosDesdeElRepositorio() throws Exception {
        when(reclamoRepository.findAll()).thenReturn(List.of(new Reclamo()));

        mockMvc.perform(get("/api/reclamos/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
