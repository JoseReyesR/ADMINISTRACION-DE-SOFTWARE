package reclamostottus.reclamos_backend.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EvidenciaTest {

    @Test
    void getterYSetterFuncionanCorrectamente() {
        Evidencia evidencia = new Evidencia();
        Reclamo reclamo = new Reclamo();
        LocalDateTime fechaSubida = LocalDateTime.now();

        evidencia.setId(5);
        evidencia.setReclamo(reclamo);
        evidencia.setNombreArchivo("foto.jpg");
        evidencia.setRutaArchivo("/uploads/foto.jpg");
        evidencia.setTipoArchivo("image/jpeg");
        evidencia.setTamanioMb(BigDecimal.valueOf(1.25));
        evidencia.setFechaSubida(fechaSubida);

        assertThat(evidencia.getId()).isEqualTo(5);
        assertThat(evidencia.getReclamo()).isSameAs(reclamo);
        assertThat(evidencia.getNombreArchivo()).isEqualTo("foto.jpg");
        assertThat(evidencia.getRutaArchivo()).isEqualTo("/uploads/foto.jpg");
        assertThat(evidencia.getTipoArchivo()).isEqualTo("image/jpeg");
        assertThat(evidencia.getTamanioMb()).isEqualTo(BigDecimal.valueOf(1.25));
        assertThat(evidencia.getFechaSubida()).isEqualTo(fechaSubida);
    }
}
