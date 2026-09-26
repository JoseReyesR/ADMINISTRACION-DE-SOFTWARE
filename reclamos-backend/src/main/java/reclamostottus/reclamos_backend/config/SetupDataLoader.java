package reclamostottus.reclamos_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
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
        // Verifica si el usuario ya existe para no duplicarlo
        if (usuarioRepository.findByCorreo("final@final").isEmpty()) {
            Usuario u = new Usuario();
            u.setTipoDocumento("DNI");
            u.setNumeroDocumento("44444441");
            u.setNombres("Usuario1");
            u.setApellidos("Final1");
            u.setCorreo("final@final");
            u.setTelefono("999999949");

            // ¡AQUÍ ESTÁ LA MAGIA! Spring encripta la palabra exacta de forma nativa
            u.setPassword(passwordEncoder.encode("12345"));
            u.setIsActive(true);

            Rol r = new Rol();
            r.setId(2); // ID 2 corresponde al Rol Administrador
            u.setRol(r);

            usuarioRepository.save(u);
            System.out.println("\n✅ USUARIO DE PRUEBA CREADO: final@final | CLAVE: 12345\n");
        }

        if (usuarioRepository.findByCorreo("test@admin").isEmpty()) {
            Usuario uTest = new Usuario();
            uTest.setTipoDocumento("DNI");
            uTest.setNumeroDocumento("77778888");
            uTest.setNombres("Usuario");
            uTest.setApellidos("Test");
            uTest.setCorreo("test@admin");
            uTest.setTelefono("999000111");

            // El motor BCrypt ahora sí está activo y encriptará "1234" de forma 100% nativa
            uTest.setPassword(passwordEncoder.encode("1234"));
            uTest.setIsActive(true);

            Rol rTest = new Rol();
            rTest.setId(2); // ID 2 para Administrador
            uTest.setRol(rTest);

            usuarioRepository.save(uTest);
            System.out.println("✅ USUARIO CREADO: test@admin | CLAVE: 1234");
        }

        // NUEVO CLIENTE DE PRUEBA
        if (usuarioRepository.findByCorreo("cliente@tottus.com").isEmpty()) {
            Usuario uCliente = new Usuario();
            uCliente.setTipoDocumento("DNI");
            uCliente.setNumeroDocumento("88889999");
            uCliente.setNombres("María");
            uCliente.setApellidos("Torres");
            uCliente.setCorreo("cliente@tottus.com");
            uCliente.setTelefono("987654321");

            // Encriptamos la clave "123456" de forma nativa
            uCliente.setPassword(passwordEncoder.encode("123456"));
            uCliente.setIsActive(true);

            Rol rCliente = new Rol();
            rCliente.setId(1); // ID 1 corresponde a ROLE_CLIENTE en tu base de datos
            uCliente.setRol(rCliente);

            usuarioRepository.save(uCliente);
            System.out.println("✅ CLIENTE CREADO: cliente@tottus.com | CLAVE: 123456");
        }

    }
}