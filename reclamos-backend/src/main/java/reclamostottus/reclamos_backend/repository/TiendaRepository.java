package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda, Integer> {
}