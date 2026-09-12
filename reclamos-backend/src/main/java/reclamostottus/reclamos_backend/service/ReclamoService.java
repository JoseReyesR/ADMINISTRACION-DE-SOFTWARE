package reclamostottus.reclamos_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.*;
import reclamostottus.reclamos_backend.repository.EvidenciaRepository;
import reclamostottus.reclamos_backend.repository.ReclamoRepository;
import reclamostottus.reclamos_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepository reclamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;

    // Métodos para el Dashboard y Seguimiento
    public List<Reclamo> listarTodos() {
        return reclamoRepository.findAll();
    }

    public Optional<Reclamo> buscarPorCodigo(String codigo) {
        return reclamoRepository.findByCodigoSeguimiento(codigo);
    }

    @Transactional
    public Reclamo registrarReclamo(ReclamoRequestDTO dto, MultipartFile archivo) {
        Reclamo nuevoReclamo = new Reclamo();

        // 1. Generar Código de Seguimiento Aleatorio (Ej: REQ-2026-8927)
        String codigo = "REQ-2026-" + (new Random().nextInt(9000) + 1000);
        nuevoReclamo.setCodigoSeguimiento(codigo);

        // 2. Gestión Inteligente del Usuario (RN-01, RN-03)
        // Buscamos si el DNI ya existe; si no, lo creamos y le asignamos el Rol 3
        // (Invitado)
        Usuario usuario = usuarioRepository.findByNumeroDocumento(dto.getNumeroDocumento())
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setTipoDocumento(dto.getTipoDocumento());
                    nuevo.setNumeroDocumento(dto.getNumeroDocumento());
                    nuevo.setNombres(dto.getNombres());
                    nuevo.setApellidos(dto.getApellidos());
                    nuevo.setCorreo(dto.getCorreo());
                    nuevo.setTelefono(dto.getCelular());

                    // Generar contraseña aleatoria irrecuperable
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    nuevo.setPassword(encoder.encode(java.util.UUID.randomUUID().toString()));

                    Rol rolInvitado = new Rol();
                    rolInvitado.setId(3);
                    nuevo.setRol(rolInvitado);

                    nuevo.setIsActive(true); // Se activa por defecto al crearlo

                    return usuarioRepository.save(nuevo);
                });
        // BLOQUEO DE SEGURIDAD: Verificar si la cuenta fue restringida
        if (!usuario.getIsActive()) {
            throw new RuntimeException("Operación denegada: Su cuenta se encuentra restringida.");
        }

        nuevoReclamo.setUsuario(usuario);

        // 3. Mapear datos del incidente (DTO -> Entidad)
        nuevoReclamo.setTipoSolicitud(dto.getTipoSolicitud());
        nuevoReclamo.setCanalCompra(dto.getCanalCompra());
        nuevoReclamo.setTienda(dto.getTienda());
        nuevoReclamo.setNumeroBoletaPedido(dto.getNumeroBoleta());

        // Convertimos el String de la fecha a LocalDate para que MySQL lo entienda
        if (dto.getFechaCompra() != null && !dto.getFechaCompra().isEmpty()) {
            nuevoReclamo.setFechaCompra(LocalDate.parse(dto.getFechaCompra()));
        }

        nuevoReclamo.setProductoImplicado(dto.getProducto());
        nuevoReclamo.setDescripcionCaso(dto.getDescripcion());

        // 4. Regla de Negocio: 15 días hábiles (Sumamos 21 días naturales por fines de
        // semana)
        nuevoReclamo.setFechaVencimiento(LocalDateTime.now().plusDays(21));

        // 5. Asignación Temporal de Llaves Foráneas (Categoría, Prioridad, Estado)
        Categoria catDefault = new Categoria();
        catDefault.setId(1);
        nuevoReclamo.setCategoria(catDefault);

        Prioridad prioDefault = new Prioridad();
        prioDefault.setId(2);
        nuevoReclamo.setPrioridad(prioDefault);

        EstadoReclamo estadoDefault = new EstadoReclamo();
        estadoDefault.setId(1); // 1 = Ingresado
        nuevoReclamo.setEstado(estadoDefault);

        // 6. Guardar el Reclamo
        Reclamo reclamoGuardado = reclamoRepository.save(nuevoReclamo);

        // 7. Guardar la Evidencia (RN-17) si es que el cliente adjuntó una foto
        if (archivo != null && !archivo.isEmpty()) {
            Evidencia evidencia = new Evidencia();
            evidencia.setReclamo(reclamoGuardado);
            evidencia.setNombreArchivo(archivo.getOriginalFilename());
            evidencia.setRutaArchivo("/uploads/" + archivo.getOriginalFilename());
            evidencia.setTipoArchivo(archivo.getContentType());

            // Convertimos el double a BigDecimal para que coincida con tu Entidad
            double sizeInMb = (double) archivo.getSize() / (1024 * 1024);
            evidencia.setTamanioMb(BigDecimal.valueOf(sizeInMb));

            evidenciaRepository.save(evidencia);
        }

        return reclamoGuardado;
    }

    // NUEVO: Obtener historial exclusivo de un cliente
    public List<Reclamo> obtenerMisCasos(String correoCliente) {
        return reclamoRepository.findByUsuarioCorreo(correoCliente);
    }
}

// NUEVO: Obtener historial exclusivo de un cliente
