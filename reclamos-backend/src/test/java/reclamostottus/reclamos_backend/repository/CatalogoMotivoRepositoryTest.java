package reclamostottus.reclamos_backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import reclamostottus.reclamos_backend.model.CatalogoMotivo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CatalogoMotivoRepositoryTest {

    @Autowired
    private CatalogoMotivoRepository catalogoMotivoRepository;

    @Test
    void findByTipoSolicitudDevuelveSoloLosMotivosDelTipoIndicado() {
        catalogoMotivoRepository.save(nuevoMotivo("Producto dañado", "Reclamo"));
        catalogoMotivoRepository.save(nuevoMotivo("Demora en entrega", "Reclamo"));
        catalogoMotivoRepository.save(nuevoMotivo("Mal trato", "Queja"));

        List<CatalogoMotivo> reclamos = catalogoMotivoRepository.findByTipoSolicitud("Reclamo");
        List<CatalogoMotivo> quejas = catalogoMotivoRepository.findByTipoSolicitud("Queja");

        assertThat(reclamos).hasSize(2);
        assertThat(quejas).hasSize(1);
    }

    @Test
    void findByTipoSolicitudDevuelveVacioCuandoNoHayCoincidencias() {
        List<CatalogoMotivo> resultado = catalogoMotivoRepository.findByTipoSolicitud("Inexistente");

        assertThat(resultado).isEmpty();
    }

    private CatalogoMotivo nuevoMotivo(String nombre, String tipo) {
        CatalogoMotivo motivo = new CatalogoMotivo();
        motivo.setNombre(nombre);
        motivo.setTipoSolicitud(tipo);
        return motivo;
    }
}
