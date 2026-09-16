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

    // 1. Guardar un nuevo comentario o cambio de estado
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

        return historialRepo.save(historial);
    }

    // 2. Leer historial para el BackOffice (Todo)
    public List<HistorialSeguimiento> obtenerHistorialCompleto(Integer reclamoId) {
        return historialRepo.findByReclamoIdOrderByFechaRegistroAsc(reclamoId);
    }

    // 3. Leer historial para el Cliente (Solo lo público)
    public List<HistorialSeguimiento> obtenerHistorialPublico(Integer reclamoId) {
        return historialRepo.findByReclamoIdAndEsInternoFalseOrderByFechaRegistroAsc(reclamoId);
    }
}