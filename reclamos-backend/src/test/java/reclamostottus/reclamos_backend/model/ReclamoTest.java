package reclamostottus.reclamos_backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReclamoTest {

    @Test
    void getterYSetterFuncionanCorrectamente() {
        Reclamo reclamo = new Reclamo();

        Usuario usuario = new Usuario();
        Categoria categoria = new Categoria();
        Prioridad prioridad = new Prioridad();
        EstadoReclamo estado = new EstadoReclamo();
        Tienda tienda = new Tienda();
        CatalogoMotivo motivo = new CatalogoMotivo();
        LocalDate fechaCompra = LocalDate.of(2026, 1, 15);
        LocalDateTime fechaRegistro = LocalDateTime.of(2026, 1, 16, 10, 0);
        LocalDateTime fechaVencimiento = LocalDateTime.of(2026, 2, 6, 10, 0);
        Evidencia evidencia = new Evidencia();
        List<Evidencia> evidencias = List.of(evidencia);

        reclamo.setId(1);
        reclamo.setCodigoSeguimiento("REQ-2026-1234");
        reclamo.setUsuario(usuario);
        reclamo.setCategoria(categoria);
        reclamo.setPrioridad(prioridad);
        reclamo.setEstado(estado);
        reclamo.setTipoSolicitud("Reclamo");
        reclamo.setCanalCompra("Online");
        reclamo.setTienda(tienda);
        reclamo.setMotivo(motivo);
        reclamo.setNumeroBoletaPedido("B-001");
        reclamo.setFechaCompra(fechaCompra);
        reclamo.setProductoImplicado("Televisor");
        reclamo.setDescripcionCaso("Descripción de prueba");
        reclamo.setFechaRegistro(fechaRegistro);
        reclamo.setFechaVencimiento(fechaVencimiento);
        reclamo.setEvidencias(evidencias);

        assertThat(reclamo.getId()).isEqualTo(1);
        assertThat(reclamo.getCodigoSeguimiento()).isEqualTo("REQ-2026-1234");
        assertThat(reclamo.getUsuario()).isSameAs(usuario);
        assertThat(reclamo.getCategoria()).isSameAs(categoria);
        assertThat(reclamo.getPrioridad()).isSameAs(prioridad);
        assertThat(reclamo.getEstado()).isSameAs(estado);
        assertThat(reclamo.getTipoSolicitud()).isEqualTo("Reclamo");
        assertThat(reclamo.getCanalCompra()).isEqualTo("Online");
        assertThat(reclamo.getTienda()).isSameAs(tienda);
        assertThat(reclamo.getMotivo()).isSameAs(motivo);
        assertThat(reclamo.getNumeroBoletaPedido()).isEqualTo("B-001");
        assertThat(reclamo.getFechaCompra()).isEqualTo(fechaCompra);
        assertThat(reclamo.getProductoImplicado()).isEqualTo("Televisor");
        assertThat(reclamo.getDescripcionCaso()).isEqualTo("Descripción de prueba");
        assertThat(reclamo.getFechaRegistro()).isEqualTo(fechaRegistro);
        assertThat(reclamo.getFechaVencimiento()).isEqualTo(fechaVencimiento);
        assertThat(reclamo.getEvidencias()).containsExactly(evidencia);
    }

    @Test
    void constructorVacioNoLanzaExcepcion() {
        assertThat(new Reclamo()).isNotNull();
    }
}
