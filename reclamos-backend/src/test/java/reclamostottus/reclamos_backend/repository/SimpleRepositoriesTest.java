package reclamostottus.reclamos_backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import reclamostottus.reclamos_backend.model.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Estos repositorios (Categoria, Estado, Evidencia, Motivo, Prioridad, Rol, Tienda)
 * solo extienden JpaRepository sin métodos propios, así que se valida que el
 * CRUD heredado de Spring Data funcione correctamente contra la base de datos real (H2).
 */
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SimpleRepositoriesTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoReclamoRepository estadoReclamoRepository;

    @Autowired
    private MotivoRepository motivoRepository;

    @Autowired
    private PrioridadRepository prioridadRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TiendaRepository tiendaRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;

    @Test
    void categoriaRepositorySaveYFindById() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria Test");
        Categoria guardada = categoriaRepository.save(categoria);

        assertThat(categoriaRepository.findById(guardada.getId())).isPresent();
    }

    @Test
    void estadoReclamoRepositorySaveYFindById() {
        EstadoReclamo estado = new EstadoReclamo();
        estado.setNombre("Estado Test");
        EstadoReclamo guardado = estadoReclamoRepository.save(estado);

        assertThat(estadoReclamoRepository.findById(guardado.getId())).isPresent();
    }

    @Test
    void motivoRepositorySaveYFindById() {
        Motivo motivo = new Motivo();
        motivo.setNombre("Motivo Test");
        motivo.setTipoSolicitud("Reclamo");
        Motivo guardado = motivoRepository.save(motivo);

        assertThat(motivoRepository.findById(guardado.getId())).isPresent();
    }

    @Test
    void prioridadRepositorySaveYFindById() {
        Prioridad prioridad = new Prioridad();
        prioridad.setNombre("Prioridad Test");
        Prioridad guardada = prioridadRepository.save(prioridad);

        assertThat(prioridadRepository.findById(guardada.getId())).isPresent();
    }

    @Test
    void rolRepositorySaveYFindById() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_TEST_" + System.nanoTime());
        Rol guardado = rolRepository.save(rol);

        assertThat(rolRepository.findById(guardado.getId())).isPresent();
    }

    @Test
    void tiendaRepositorySaveYFindById() {
        Tienda tienda = new Tienda();
        tienda.setNombre("Tienda Test");
        Tienda guardada = tiendaRepository.save(tienda);

        assertThat(tiendaRepository.findById(guardada.getId())).isPresent();
    }

    @Test
    void evidenciaRepositoryFindAllNoLanzaExcepcionSinDatos() {
        assertThat(evidenciaRepository.findAll()).isNotNull();
    }
}
