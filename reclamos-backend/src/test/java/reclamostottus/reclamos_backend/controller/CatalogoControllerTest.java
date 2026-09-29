package reclamostottus.reclamos_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reclamostottus.reclamos_backend.model.CatalogoMotivo;
import reclamostottus.reclamos_backend.model.Categoria;
import reclamostottus.reclamos_backend.model.Tienda;
import reclamostottus.reclamos_backend.repository.CatalogoMotivoRepository;
import reclamostottus.reclamos_backend.repository.CategoriaRepository;
import reclamostottus.reclamos_backend.repository.TiendaRepository;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CatalogoControllerTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @Mock
    private CatalogoMotivoRepository motivoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CatalogoController catalogoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(catalogoController).build();
    }

    @Test
    void listarTiendasDevuelveLaListaDeTiendas() throws Exception {
        Tienda tienda = new Tienda();
        tienda.setId(1);
        tienda.setNombre("Tottus San Miguel");
        when(tiendaRepository.findAll()).thenReturn(List.of(tienda));

        mockMvc.perform(get("/api/catalogos/tiendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Tottus San Miguel"));
    }

    @Test
    void listarMotivosDevuelveLaListaDeMotivos() throws Exception {
        CatalogoMotivo motivo = new CatalogoMotivo();
        motivo.setId(1);
        motivo.setNombre("Producto dañado");
        when(motivoRepository.findAll()).thenReturn(List.of(motivo));

        mockMvc.perform(get("/api/catalogos/motivos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto dañado"));
    }

    @Test
    void listarCategoriasDevuelveLaListaDeCategorias() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Producto defectuoso");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/catalogos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto defectuoso"));
    }
}
