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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReclamoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReclamoRepository reclamoRepository;

    private Usuario usuario;
    private Categoria categoria;
    private Prioridad prioridad;
    private EstadoReclamo estado;
    private Tienda tienda;
    private CatalogoMotivo motivo;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_CLIENTE_" + System.nanoTime());
        entityManager.persist(rol);

        usuario = new Usuario();
        usuario.setTipoDocumento("DNI");
        usuario.setNumeroDocumento("40000004");
        usuario.setNombres("Carlos");
        usuario.setApellidos("Ramos");
        usuario.setCorreo("carlos@correo.com");
        usuario.setPassword("hash");
        usuario.setRol(rol);
        usuario.setIsActive(true);
        entityManager.persist(usuario);

        categoria = new Categoria();
        categoria.setNombre("Producto defectuoso");
        entityManager.persist(categoria);

        prioridad = new Prioridad();
        prioridad.setNombre("Media");
        entityManager.persist(prioridad);

        estado = new EstadoReclamo();
        estado.setNombre("Registrado");
        entityManager.persist(estado);

        tienda = new Tienda();
        tienda.setNombre("Tottus Test");
        entityManager.persist(tienda);

        motivo = new CatalogoMotivo();
        motivo.setNombre("Demora");
        motivo.setTipoSolicitud("Reclamo");
        entityManager.persist(motivo);

        entityManager.flush();
    }

    private Reclamo nuevoReclamo(String codigo) {
        Reclamo reclamo = new Reclamo();
        reclamo.setCodigoSeguimiento(codigo);
        reclamo.setUsuario(usuario);
        reclamo.setCategoria(categoria);
        reclamo.setPrioridad(prioridad);
        reclamo.setEstado(estado);
        reclamo.setTipoSolicitud("Reclamo");
        reclamo.setCanalCompra("Online");
        reclamo.setTienda(tienda);
        reclamo.setMotivo(motivo);
        reclamo.setProductoImplicado("Producto X");
        reclamo.setDescripcionCaso("Descripción");
        reclamo.setFechaVencimiento(LocalDateTime.now().plusDays(21));
        return reclamo;
    }

    @Test
    void findByCodigoSeguimientoDevuelveElReclamoCuandoExiste() {
        entityManager.persistAndFlush(nuevoReclamo("REQ-2026-0001"));

        Optional<Reclamo> resultado = reclamoRepository.findByCodigoSeguimiento("REQ-2026-0001");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuario().getCorreo()).isEqualTo("carlos@correo.com");
    }

    @Test
    void findByCodigoSeguimientoDevuelveVacioCuandoNoExiste() {
        Optional<Reclamo> resultado = reclamoRepository.findByCodigoSeguimiento("NO-EXISTE");

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByUsuarioCorreoDevuelveTodosLosReclamosDelUsuario() {
        entityManager.persistAndFlush(nuevoReclamo("REQ-2026-0002"));
        entityManager.persistAndFlush(nuevoReclamo("REQ-2026-0003"));

        List<Reclamo> resultado = reclamoRepository.findByUsuarioCorreo("carlos@correo.com");

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findByUsuarioCorreoDevuelveListaVaciaCuandoNoHayCoincidencias() {
        List<Reclamo> resultado = reclamoRepository.findByUsuarioCorreo("nadie@correo.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByUsuarioNumeroDocumentoDevuelveLosReclamosDelDni() {
        entityManager.persistAndFlush(nuevoReclamo("REQ-2026-0004"));

        List<Reclamo> resultado = reclamoRepository.findByUsuarioNumeroDocumento("40000004");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigoSeguimiento()).isEqualTo("REQ-2026-0004");
    }

    @Test
    void findByUsuarioNumeroDocumentoDevuelveListaVaciaCuandoNoHayCoincidencias() {
        List<Reclamo> resultado = reclamoRepository.findByUsuarioNumeroDocumento("00000000");

        assertThat(resultado).isEmpty();
    }
}
