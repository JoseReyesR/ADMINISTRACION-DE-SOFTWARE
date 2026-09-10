package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Spring Data JPA asume el resto
}