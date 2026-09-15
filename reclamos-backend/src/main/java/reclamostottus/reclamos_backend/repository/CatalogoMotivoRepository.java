package reclamostottus.reclamos_backend.repository;

import reclamostottus.reclamos_backend.model.CatalogoMotivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatalogoMotivoRepository extends JpaRepository<CatalogoMotivo, Integer> {
    // Esto servirá para filtrar en Angular (solo Mostrar Motivos de Reclamo o
    // Queja)
    List<CatalogoMotivo> findByTipoSolicitud(String tipoSolicitud);
}