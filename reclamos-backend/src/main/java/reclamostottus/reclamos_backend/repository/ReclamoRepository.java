package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {
    Optional<Reclamo> findByCodigoSeguimiento(String codigoSeguimiento);

    // NUEVO: Busca todos los reclamos asociados a un correo específico
    List<Reclamo> findByUsuarioCorreo(String correo);

    // Buscar todos los reclamos asociados a un DNI específico
    List<Reclamo> findByUsuarioNumeroDocumento(String numeroDocumento);
}
