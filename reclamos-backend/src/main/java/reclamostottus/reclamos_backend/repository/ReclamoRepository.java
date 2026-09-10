package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {
    Optional<Reclamo> findByCodigoSeguimiento(String codigoSeguimiento);
}