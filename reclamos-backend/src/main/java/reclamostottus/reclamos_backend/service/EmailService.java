package reclamostottus.reclamos_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reclamostottus.reclamos_backend.model.Reclamo;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // FORZAMOS EL CORREO A TU DIRECCIÓN DE PRUEBAS
    private final String CORREO_DESTINO = "pruebasdecorreoupn@gmail.com";

    @Async
    public void enviarCorreoRegistro(Reclamo reclamo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(CORREO_DESTINO);
            mensaje.setSubject("Confirmación de Registro - Caso " + reclamo.getCodigoSeguimiento());

            String contenido = "¡Hola " + reclamo.getUsuario().getNombres() + "!\n\n"
                    + "Tu caso ha sido ingresado exitosamente en nuestro sistema.\n\n"
                    + "RESUMEN DE TU CASO:\n"
                    + "- Código: " + reclamo.getCodigoSeguimiento() + "\n"
                    + "- DNI Ingresado: " + reclamo.getUsuario().getNumeroDocumento() + "\n"
                    + "- Tipo: " + reclamo.getTipoSolicitud() + "\n"
                    + "- Canal: " + reclamo.getCanalCompra() + "\n"
                    + "- Producto: " + reclamo.getProductoImplicado() + "\n"
                    + "- Descripción: " + reclamo.getDescripcionCaso() + "\n\n"
                    + "Plazo máximo legal de respuesta: 15 días hábiles.\n\n"
                    + "Saludos cordiales,\nEquipo de Atención Tottus.";

            mensaje.setText(contenido);
            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error silencioso al enviar correo de registro: " + e.getMessage());
        }
    }

    @Async
    public void enviarCorreoActualizacion(String codigoCaso, String detalleCambio) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(CORREO_DESTINO);
            mensaje.setSubject("ACTUALIZACION DE TU CASO - " + codigoCaso);

            String contenido = "Hola,\n\n"
                    + "Te informamos que ha habido una actualización en tu reclamo.\n\n"
                    + "NUEVO ESTADO O NOTA:\n"
                    + detalleCambio + "\n\n"
                    + "Puedes revisar más detalles ingresando a nuestro portal.\n\n"
                    + "Saludos cordiales,\nEquipo de Atención Tottus.";

            mensaje.setText(contenido);
            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error silencioso al enviar correo de actualización: " + e.getMessage());
        }
    }
}