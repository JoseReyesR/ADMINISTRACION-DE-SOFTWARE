package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.Motivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotivoRepository extends JpaRepository<Motivo, Integer> {
    // Spring Boot implementará automáticamente las consultas
}