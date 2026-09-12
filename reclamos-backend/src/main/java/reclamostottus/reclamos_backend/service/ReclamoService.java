package reclamostottus.reclamos_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.Categoria;
import reclamostottus.reclamos_backend.model.EstadoReclamo;
import reclamostottus.reclamos_backend.model.Prioridad;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.model.Usuario;
import reclamostottus.reclamos_backend.repository.ReclamoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepository reclamoRepository;

    // Métodos para el Dashboard y Seguimiento
    public List<Reclamo> listarTodos() {
        return reclamoRepository.findAll();
    }

    public Optional<Reclamo> buscarPorCodigo(String codigo) {
        // Asegúrate de tener este método en tu ReclamoRepository: Optional<Reclamo>
        // findByCodigoSeguimiento(String codigo);
        return reclamoRepository.findByCodigoSeguimiento(codigo);
    }

    @Transactional
    public Reclamo registrarReclamo(ReclamoRequestDTO dto) {
        Reclamo nuevoReclamo = new Reclamo();

        // 1. Generar Código de Seguimiento Aleatorio (Ej: REQ-2026-8927)
        String codigo = "REQ-2026-" + (new Random().nextInt(9000) + 1000);
        nuevoReclamo.setCodigoSeguimiento(codigo);

        // 2. Mapear datos del DTO a la Entidad
        nuevoReclamo.setTipoSolicitud(dto.getTipoSolicitud());
        nuevoReclamo.setCanalCompra(dto.getCanalCompra());
        nuevoReclamo.setTienda(dto.getTienda());
        nuevoReclamo.setNumeroBoletaPedido(dto.getNumeroBoleta());
        nuevoReclamo.setProductoImplicado(dto.getProducto());
        nuevoReclamo.setDescripcionCaso(dto.getDescripcion());

        // 3. Regla de Negocio: 15 días hábiles (Sumamos 21 días naturales por fines de
        // semana)
        nuevoReclamo.setFechaVencimiento(LocalDateTime.now().plusDays(21));

        /*
         * IMPORTANTE: Para que no falle la inserción por las llaves foráneas (Not
         * Null),
         * debes asignar las entidades relacionales (Usuario, Categoria, Prioridad,
         * Estado).
         * Aquí asumimos que ya las buscaste previamente con sus repositorios.
         * Ejemplo: nuevoReclamo.setUsuario(usuarioRepository.findById(1).get());
         */
        // ASIGNACIÓN TEMPORAL DE LLAVES FORÁNEAS (Para pruebas)
        // En un flujo real, estos IDs vendrían de buscar el correo o la categoría en la
        // BD

        // Asignamos al usuario "Invitado" que creamos con ID 1 en el script SQL
        Usuario usuarioInvitado = new Usuario();
        usuarioInvitado.setId(1);
        nuevoReclamo.setUsuario(usuarioInvitado);

        // Asignamos Categoría 1 (Precio y Cobro)
        Categoria catDefault = new Categoria();
        catDefault.setId(1);
        nuevoReclamo.setCategoria(catDefault);

        // Asignamos Prioridad 2 (Media)
        Prioridad prioDefault = new Prioridad();
        prioDefault.setId(2);
        nuevoReclamo.setPrioridad(prioDefault);

        // Asignamos Estado 1 (Ingresado)
        EstadoReclamo estadoDefault = new EstadoReclamo();
        estadoDefault.setId(1);
        nuevoReclamo.setEstado(estadoDefault);

        // 4. Guardar en Base de Datos
        return reclamoRepository.save(nuevoReclamo);
    }
}