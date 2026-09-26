package reclamostottus.reclamos_backend.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void loginRequestDTOGetterYSetter() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setCorreo("cliente@tottus.com");
        dto.setPassword("123456");

        assertThat(dto.getCorreo()).isEqualTo("cliente@tottus.com");
        assertThat(dto.getPassword()).isEqualTo("123456");
    }

    @Test
    void historialRequestDTOGetterYSetter() {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(1);
        dto.setUsuarioResponsableId(2);
        dto.setEstadoNuevoId(3);
        dto.setComentario("Nota de seguimiento");
        dto.setEsInterno(true);

        assertThat(dto.getReclamoId()).isEqualTo(1);
        assertThat(dto.getUsuarioResponsableId()).isEqualTo(2);
        assertThat(dto.getEstadoNuevoId()).isEqualTo(3);
        assertThat(dto.getComentario()).isEqualTo("Nota de seguimiento");
        assertThat(dto.getEsInterno()).isTrue();
    }

    @Test
    void reclamoRequestDTOGetterYSetter() {
        ReclamoRequestDTO dto = new ReclamoRequestDTO();
        dto.setTipoDocumento("DNI");
        dto.setNumeroDocumento("87654321");
        dto.setNombres("Ana");
        dto.setApellidos("Gomez");
        dto.setCorreo("ana@correo.com");
        dto.setCelular("999888777");
        dto.setTipoSolicitud("Reclamo");
        dto.setCanalCompra("Tienda física");
        dto.setTienda("1");
        dto.setNumeroBoleta("B-123");
        dto.setMotivo("2");
        dto.setProducto("Licuadora");
        dto.setDescripcion("El producto llegó dañado");
        dto.setFechaCompra("2026-01-10");
        dto.setCategoria("3");

        assertThat(dto.getTipoDocumento()).isEqualTo("DNI");
        assertThat(dto.getNumeroDocumento()).isEqualTo("87654321");
        assertThat(dto.getNombres()).isEqualTo("Ana");
        assertThat(dto.getApellidos()).isEqualTo("Gomez");
        assertThat(dto.getCorreo()).isEqualTo("ana@correo.com");
        assertThat(dto.getCelular()).isEqualTo("999888777");
        assertThat(dto.getTipoSolicitud()).isEqualTo("Reclamo");
        assertThat(dto.getCanalCompra()).isEqualTo("Tienda física");
        assertThat(dto.getTienda()).isEqualTo("1");
        assertThat(dto.getNumeroBoleta()).isEqualTo("B-123");
        assertThat(dto.getMotivo()).isEqualTo("2");
        assertThat(dto.getProducto()).isEqualTo("Licuadora");
        assertThat(dto.getDescripcion()).isEqualTo("El producto llegó dañado");
        assertThat(dto.getFechaCompra()).isEqualTo("2026-01-10");
        assertThat(dto.getCategoria()).isEqualTo("3");
    }
}
