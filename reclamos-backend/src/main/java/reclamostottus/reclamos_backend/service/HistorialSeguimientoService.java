package reclamostottus.reclamos_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reclamostottus.reclamos_backend.dto.HistorialRequestDTO;
import reclamostottus.reclamos_backend.model.*;
import reclamostottus.reclamos_backend.repository.*;

import java.util.List;

@Service
public class HistorialSeguimientoService {

    @Autowired
    private HistorialSeguimientoRepository historialRepo;

    @Autowired
    private ReclamoRepository reclamoRepo;

    // 1. Inyectamos tu nuevo servicio de correos
    @Autowired
    private EmailService emailService;

    // 2. Guardar un nuevo comentario o cambio de estado
    public HistorialSeguimiento registrarHistorial(HistorialRequestDTO dto) {
        Reclamo reclamo = reclamoRepo.findById(dto.getReclamoId())
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado"));

        HistorialSeguimiento historial = new HistorialSeguimiento();
        historial.setReclamo(reclamo);
        historial.setComentario(dto.getComentario());
        historial.setEsInterno(dto.getEsInterno());

        // Simulación de usuario (luego lo tomaremos del Token)
        Usuario admin = new Usuario();
        admin.setId(dto.getUsuarioResponsableId());
        historial.setUsuarioResponsable(admin);

        // Si hay cambio de estado, actualizamos el reclamo principal
        if (dto.getEstadoNuevoId() != null && !dto.getEstadoNuevoId().equals(reclamo.getEstado().getId())) {
            historial.setEstadoAnterior(reclamo.getEstado());

            EstadoReclamo nuevoEstado = new EstadoReclamo();
            nuevoEstado.setId(dto.getEstadoNuevoId());
            historial.setEstadoNuevo(nuevoEstado);

            reclamo.setEstado(nuevoEstado);
            reclamoRepo.save(reclamo); // Actualiza la tabla reclamos
        }

        // Guardamos el historial en la base de datos
        HistorialSeguimiento historialGuardado = historialRepo.save(historial);

        // 3. LÓGICA DE CORREO: Solo enviamos si NO es una nota interna
        if (dto.getEsInterno() != null && !dto.getEsInterno()) {
            String detalleCorreo = "Se ha agregado una nueva nota pública a tu caso:\n\n\"" + dto.getComentario()
                    + "\"";

            // Si además se cambió el estado, lo mencionamos en el correo
            if (dto.getEstadoNuevoId() != null) {
                detalleCorreo += "\n\nAdemás, el estado de tu reclamo ha sido actualizado.";
            }

            emailService.enviarCorreoActualizacion(reclamo.getCodigoSeguimiento(), detalleCorreo);
        }

        return historialGuardado;
    }

    // 3. Leer historial para el BackOffice (Todo)
    public List<HistorialSeguimiento> obtenerHistorialCompleto(Integer reclamoId) {
        return historialRepo.findByReclamoIdOrderByFechaRegistroAsc(reclamoId);
    }

    // 4. Leer historial para el Cliente (Solo lo público)
    public List<HistorialSeguimiento> obtenerHistorialPublico(Integer reclamoId) {
        return historialRepo.findByReclamoIdAndEsInternoFalseOrderByFechaRegistroAsc(reclamoId);
    }
}