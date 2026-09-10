package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Integer> {
}