package reclamostottus.reclamos_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reclamostottus.reclamos_backend.model.Rol;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;

@Component
public class SetupDataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        crearUsuarioSiNoExiste("soporte@tottus.com", "Soporte", "88888888", 2); // ROLE_ADMIN o ROLE_TECNICO
        crearUsuarioSiNoExiste("dev@dev.com", "Desarrollador", "77777777", 2);
    }

    private void crearUsuarioSiNoExiste(String correo, String nombre, String dni, Integer rolId) {
        if (usuarioRepository.findByCorreo(correo).isEmpty()) {
            Usuario u = new Usuario();
            u.setTipoDocumento("DNI");
            u.setNumeroDocumento(dni);
            u.setNombres(nombre);
            u.setApellidos("Tottus");
            u.setCorreo(correo);
            u.setTelefono("999999999");
            // Spring Boot encripta "123456" de forma 100% segura y compatible
            u.setPassword(passwordEncoder.encode("123456"));
            u.setIsActive(true);

            Rol r = new Rol();
            r.setId(rolId);
            u.setRol(r);

            usuarioRepository.save(u);
            System.out.println("✅ Usuario '" + correo + "' con clave '123456' creado en la BD.");
        }
    }
}