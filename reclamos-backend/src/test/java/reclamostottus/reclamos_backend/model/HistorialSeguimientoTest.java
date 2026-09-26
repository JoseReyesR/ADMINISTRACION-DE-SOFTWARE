package reclamostottus.reclamos_backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialSeguimientoTest {

    @Test
    void getterYSetterFuncionanCorrectamente() {
        HistorialSeguimiento historial = new HistorialSeguimiento();
        Reclamo reclamo = new Reclamo();
        Usuario responsable = new Usuario();
        EstadoReclamo anterior = new EstadoReclamo();
        EstadoReclamo nuevo = new EstadoReclamo();
        LocalDateTime fechaRegistro = LocalDateTime.now();

        historial.setId(7);
        historial.setReclamo(reclamo);
        historial.setUsuarioResponsable(responsable);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setComentario("Se revisó el caso");
        historial.setEsInterno(true);
        historial.setFechaRegistro(fechaRegistro);

        assertThat(historial.getId()).isEqualTo(7);
        assertThat(historial.getReclamo()).isSameAs(reclamo);
        assertThat(historial.getUsuarioResponsable()).isSameAs(responsable);
        assertThat(historial.getEstadoAnterior()).isSameAs(anterior);
        assertThat(historial.getEstadoNuevo()).isSameAs(nuevo);
        assertThat(historial.getComentario()).isEqualTo("Se revisó el caso");
        assertThat(historial.getEsInterno()).isTrue();
        assertThat(historial.getFechaRegistro()).isEqualTo(fechaRegistro);
    }

    @Test
    void valorPorDefectoDeEsInternoEsFalso() {
        HistorialSeguimiento historial = new HistorialSeguimiento();
        assertThat(historial.getEsInterno()).isFalse();
    }
}
