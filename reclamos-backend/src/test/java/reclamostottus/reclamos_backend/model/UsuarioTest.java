package reclamostottus.reclamos_backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    void getterYSetterFuncionanCorrectamente() {
        Usuario usuario = new Usuario();
        Rol rol = new Rol();
        LocalDateTime fechaRegistro = LocalDateTime.now();

        usuario.setId(10);
        usuario.setTipoDocumento("DNI");
        usuario.setNumeroDocumento("12345678");
        usuario.setNombres("Juan");
        usuario.setApellidos("Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setTelefono("999999999");
        usuario.setDireccion("Av. Siempre Viva 123");
        usuario.setPassword("hash");
        usuario.setRol(rol);
        usuario.setFechaRegistro(fechaRegistro);
        usuario.setIsActive(false);

        assertThat(usuario.getId()).isEqualTo(10);
        assertThat(usuario.getTipoDocumento()).isEqualTo("DNI");
        assertThat(usuario.getNumeroDocumento()).isEqualTo("12345678");
        assertThat(usuario.getNombres()).isEqualTo("Juan");
        assertThat(usuario.getApellidos()).isEqualTo("Perez");
        assertThat(usuario.getCorreo()).isEqualTo("juan@correo.com");
        assertThat(usuario.getTelefono()).isEqualTo("999999999");
        assertThat(usuario.getDireccion()).isEqualTo("Av. Siempre Viva 123");
        assertThat(usuario.getPassword()).isEqualTo("hash");
        assertThat(usuario.getRol()).isSameAs(rol);
        assertThat(usuario.getFechaRegistro()).isEqualTo(fechaRegistro);
        assertThat(usuario.getIsActive()).isFalse();
    }

    @Test
    void valorPorDefectoDeIsActiveEsVerdadero() {
        Usuario usuario = new Usuario();
        assertThat(usuario.getIsActive()).isTrue();
    }
}
