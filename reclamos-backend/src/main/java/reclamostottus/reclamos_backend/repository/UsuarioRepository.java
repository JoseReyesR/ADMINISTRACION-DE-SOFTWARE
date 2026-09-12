package reclamostottus.reclamos_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reclamostottus.reclamos_backend.model.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    // Agregamos la búsqueda por correo para el login
    Optional<Usuario> findByCorreo(String correo);
}