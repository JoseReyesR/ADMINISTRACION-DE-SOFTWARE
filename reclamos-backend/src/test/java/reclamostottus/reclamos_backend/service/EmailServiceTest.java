package reclamostottus.reclamos_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.model.Usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Reclamo reclamo;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setNombres("Ana");
        usuario.setNumeroDocumento("12345678");

        reclamo = new Reclamo();
        reclamo.setCodigoSeguimiento("REQ-2026-0001");
        reclamo.setUsuario(usuario);
        reclamo.setTipoSolicitud("Reclamo");
        reclamo.setCanalCompra("Online");
        reclamo.setProductoImplicado("Licuadora");
        reclamo.setDescripcionCaso("Llegó dañado");
    }

    @Test
    void enviarCorreoRegistroEnviaElMensajeConLosDatosDelReclamo() {
        emailService.enviarCorreoRegistro(reclamo);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensajeEnviado = captor.getValue();
        assertThat(mensajeEnviado.getTo()).containsExactly("pruebasdecorreoupn@gmail.com");
        assertThat(mensajeEnviado.getSubject()).contains("REQ-2026-0001");
        assertThat(mensajeEnviado.getText()).contains("Ana").contains("Licuadora");
    }

    @Test
    void enviarCorreoRegistroNoLanzaExcepcionSiFallaElEnvio() {
        doThrow(new RuntimeException("SMTP no disponible")).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.enviarCorreoRegistro(reclamo);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void enviarCorreoActualizacionEnviaElMensajeConElDetalleDelCambio() {
        emailService.enviarCorreoActualizacion("REQ-2026-0001", "Tu estado cambió a En Proceso");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensajeEnviado = captor.getValue();
        assertThat(mensajeEnviado.getSubject()).contains("REQ-2026-0001");
        assertThat(mensajeEnviado.getText()).contains("En Proceso");
    }

    @Test
    void enviarCorreoActualizacionNoLanzaExcepcionSiFallaElEnvio() {
        doThrow(new RuntimeException("SMTP no disponible")).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.enviarCorreoActualizacion("REQ-2026-0001", "detalle");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
