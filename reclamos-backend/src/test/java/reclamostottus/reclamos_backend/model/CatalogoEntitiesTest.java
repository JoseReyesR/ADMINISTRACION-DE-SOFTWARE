package reclamostottus.reclamos_backend.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Agrupa las pruebas de getters/setters de las entidades de catálogo,
 * que comparten la misma forma simple (id + nombre [+ campo extra]).
 */
class CatalogoEntitiesTest {

    @Test
    void rolGetterYSetter() {
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("ROLE_ADMIN");

        assertThat(rol.getId()).isEqualTo(1);
        assertThat(rol.getNombre()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void prioridadGetterYSetter() {
        Prioridad prioridad = new Prioridad();
        prioridad.setId(2);
        prioridad.setNombre("Alta");

        assertThat(prioridad.getId()).isEqualTo(2);
        assertThat(prioridad.getNombre()).isEqualTo("Alta");
    }

    @Test
    void tiendaGetterYSetter() {
        Tienda tienda = new Tienda();
        tienda.setId(3);
        tienda.setNombre("Tottus San Miguel");

        assertThat(tienda.getId()).isEqualTo(3);
        assertThat(tienda.getNombre()).isEqualTo("Tottus San Miguel");
    }

    @Test
    void estadoReclamoGetterYSetter() {
        EstadoReclamo estado = new EstadoReclamo();
        estado.setId(4);
        estado.setNombre("En proceso");

        assertThat(estado.getId()).isEqualTo(4);
        assertThat(estado.getNombre()).isEqualTo("En proceso");
    }

    @Test
    void categoriaGetterYSetter() {
        Categoria categoria = new Categoria();
        categoria.setId(5);
        categoria.setNombre("Producto defectuoso");

        assertThat(categoria.getId()).isEqualTo(5);
        assertThat(categoria.getNombre()).isEqualTo("Producto defectuoso");
    }

    @Test
    void motivoGetterYSetter() {
        Motivo motivo = new Motivo();
        motivo.setId(6);
        motivo.setNombre("Producto dañado");
        motivo.setTipoSolicitud("Reclamo");

        assertThat(motivo.getId()).isEqualTo(6);
        assertThat(motivo.getNombre()).isEqualTo("Producto dañado");
        assertThat(motivo.getTipoSolicitud()).isEqualTo("Reclamo");
    }

    @Test
    void catalogoMotivoGetterYSetter() {
        CatalogoMotivo catalogoMotivo = new CatalogoMotivo();
        catalogoMotivo.setId(7);
        catalogoMotivo.setNombre("Demora en la entrega");
        catalogoMotivo.setTipoSolicitud("Queja");

        assertThat(catalogoMotivo.getId()).isEqualTo(7);
        assertThat(catalogoMotivo.getNombre()).isEqualTo("Demora en la entrega");
        assertThat(catalogoMotivo.getTipoSolicitud()).isEqualTo("Queja");
    }
}
