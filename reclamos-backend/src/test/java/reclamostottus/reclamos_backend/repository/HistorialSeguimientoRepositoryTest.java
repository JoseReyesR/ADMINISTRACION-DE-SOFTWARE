package reclamostottus.reclamos_backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import reclamostottus.reclamos_backend.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HistorialSeguimientoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HistorialSeguimientoRepository historialRepository;

    private Reclamo reclamo;
    private Usuario responsable;
    private EstadoReclamo estado;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMIN_" + System.nanoTime());
        entityManager.persist(rol);

        responsable = new Usuario();
        responsable.setTipoDocumento("DNI");
        responsable.setNumeroDocumento("50000005");
        responsable.setNombres("Admin");
        responsable.setApellidos("Uno");
        responsable.setCorreo("admin@correo.com");
        responsable.setPassword("hash");
        responsable.setRol(rol);
        responsable.setIsActive(true);
        entityManager.persist(responsable);

        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria");
        entityManager.persist(categoria);

        Prioridad prioridad = new Prioridad();
        prioridad.setNombre("Alta");
        entityManager.persist(prioridad);

        estado = new EstadoReclamo();
        estado.setNombre("Registrado");
        entityManager.persist(estado);

        Tienda tienda = new Tienda();
        tienda.setNombre("Tottus");
        entityManager.persist(tienda);

        CatalogoMotivo motivo = new CatalogoMotivo();
        motivo.setNombre("Motivo");
        motivo.setTipoSolicitud("Reclamo");
        entityManager.persist(motivo);

        reclamo = new Reclamo();
        reclamo.setCodigoSeguimiento("REQ-2026-0010");
        reclamo.setUsuario(responsable);
        reclamo.setCategoria(categoria);
        reclamo.setPrioridad(prioridad);
        reclamo.setEstado(estado);
        reclamo.setTipoSolicitud("Reclamo");
        reclamo.setTienda(tienda);
        reclamo.setMotivo(motivo);
        reclamo.setDescripcionCaso("Descripción");
        reclamo.setFechaVencimiento(LocalDateTime.now().plusDays(21));
        entityManager.persist(reclamo);

        entityManager.flush();
    }

    private HistorialSeguimiento nuevoHistorial(String comentario, boolean esInterno) {
        HistorialSeguimiento historial = new HistorialSeguimiento();
        historial.setReclamo(reclamo);
        historial.setUsuarioResponsable(responsable);
        historial.setEstadoNuevo(estado);
        historial.setComentario(comentario);
        historial.setEsInterno(esInterno);
        return historial;
    }

    @Test
    void findByReclamoIdOrderByFechaRegistroAscDevuelveTodoElHistorial() {
        entityManager.persistAndFlush(nuevoHistorial("Nota interna", true));
        entityManager.persistAndFlush(nuevoHistorial("Nota pública", false));

        List<HistorialSeguimiento> resultado = historialRepository
                .findByReclamoIdOrderByFechaRegistroAsc(reclamo.getId());

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findByReclamoIdOrderByFechaRegistroAscDevuelveListaVaciaSinHistorial() {
        List<HistorialSeguimiento> resultado = historialRepository
                .findByReclamoIdOrderByFechaRegistroAsc(reclamo.getId());

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByReclamoIdAndEsInternoFalseOrderByFechaRegistroAscSoloDevuelvePublicas() {
        entityManager.persistAndFlush(nuevoHistorial("Nota interna", true));
        entityManager.persistAndFlush(nuevoHistorial("Nota pública", false));

        List<HistorialSeguimiento> resultado = historialRepository
                .findByReclamoIdAndEsInternoFalseOrderByFechaRegistroAsc(reclamo.getId());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getComentario()).isEqualTo("Nota pública");
    }
}
