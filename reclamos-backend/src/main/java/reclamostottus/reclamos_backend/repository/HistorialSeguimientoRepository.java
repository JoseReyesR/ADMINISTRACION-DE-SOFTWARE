package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.HistorialSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialSeguimientoRepository extends JpaRepository<HistorialSeguimiento, Integer> {

    // Para el BackOffice: Trae TODO el historial de un reclamo ordenado por fecha
    List<HistorialSeguimiento> findByReclamoIdOrderByFechaRegistroAsc(Integer reclamoId);

    // Para el Cliente (Logueado o Invitado): Trae SOLO los mensajes públicos
    List<HistorialSeguimiento> findByReclamoIdAndEsInternoFalseOrderByFechaRegistroAsc(Integer reclamoId);
}