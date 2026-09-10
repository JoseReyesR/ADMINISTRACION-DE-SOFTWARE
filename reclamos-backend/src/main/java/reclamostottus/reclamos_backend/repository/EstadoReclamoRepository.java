package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.EstadoReclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoReclamoRepository extends JpaRepository<EstadoReclamo, Integer> {
}