package reclamostottus.reclamos_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.*;
import reclamostottus.reclamos_backend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// modificado: Importaciones necesarias para manipular archivos y carpetas físicas
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepository reclamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;

    @Autowired
    private EstadoReclamoRepository estadoRepository;

    @Autowired
    private PrioridadRepository prioridadRepository;

    @Autowired
    private CatalogoMotivoRepository motivoRepository;

    @Autowired
    private TiendaRepository tiendaRepository;

    // 1. Añade esto en la parte superior de ReclamoService, debajo de tus otros
    // @Autowired
    @Autowired
    private EmailService emailService;

    // Métodos para el Dashboard y Seguimiento
    public List<Reclamo> listarTodos() {
        return reclamoRepository.findAll();
    }

    public Optional<Reclamo> buscarPorCodigo(String codigo) {
        return reclamoRepository.findByCodigoSeguimiento(codigo);
    }

    public Optional<Reclamo> seguimientoSeguroInvitado(String codigo, String dni) {
        Optional<Reclamo> reclamo = reclamoRepository.findByCodigoSeguimiento(codigo);
        if (reclamo.isPresent() && reclamo.get().getUsuario().getNumeroDocumento().equals(dni)) {
            return reclamo;
        }
        return Optional.empty();
    }

    public List<Reclamo> obtenerMisCasos(String correo) {
        return reclamoRepository.findAll().stream()
                .filter(r -> r.getUsuario().getCorreo().equals(correo))
                .toList();
    }

    @Transactional
    public Reclamo registrarReclamo(ReclamoRequestDTO dto, MultipartFile archivo) {
        Reclamo nuevoReclamo = new Reclamo();

        // 1. Generar Código de Seguimiento Aleatorio
        String codigo = "REQ-2026-" + (new Random().nextInt(9000) + 1000);
        nuevoReclamo.setCodigoSeguimiento(codigo);

        // 2. Gestión Inteligente del Usuario
        Usuario usuario = usuarioRepository.findByNumeroDocumento(dto.getNumeroDocumento())
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setTipoDocumento(dto.getTipoDocumento());
                    nuevo.setNumeroDocumento(dto.getNumeroDocumento());
                    nuevo.setNombres(dto.getNombres());
                    nuevo.setApellidos(dto.getApellidos());
                    nuevo.setCorreo(dto.getCorreo());
                    nuevo.setTelefono(dto.getCelular());

                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    nuevo.setPassword(encoder.encode(java.util.UUID.randomUUID().toString()));

                    Rol rolInvitado = new Rol();
                    rolInvitado.setId(3);
                    nuevo.setRol(rolInvitado);
                    nuevo.setIsActive(true);

                    return usuarioRepository.save(nuevo);
                });

        if (!usuario.getIsActive()) {
            throw new RuntimeException("Operación denegada: Su cuenta se encuentra restringida.");
        }

        nuevoReclamo.setUsuario(usuario);

        // 3. Mapear datos del incidente (DTO -> Entidad)
        nuevoReclamo.setTipoSolicitud(dto.getTipoSolicitud());
        nuevoReclamo.setCanalCompra(dto.getCanalCompra());

        // --- MAPEO SEGURO DE LA TIENDA ---
        if (dto.getTienda() != null && !dto.getTienda().isEmpty()) {
            try {
                Integer tiendaId = Integer.parseInt(dto.getTienda());
                Tienda tiendaReal = tiendaRepository.findById(tiendaId)
                        .orElseThrow(() -> new RuntimeException("La tienda seleccionada no existe"));
                nuevoReclamo.setTienda(tiendaReal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("El ID de la tienda es inválido.");
            }
        }

        // --- MAPEO SEGURO DEL MOTIVO (Usando tu CatalogoMotivo) ---
        if (dto.getMotivo() != null && !dto.getMotivo().isEmpty()) {
            try {
                Integer motivoId = Integer.parseInt(dto.getMotivo());
                CatalogoMotivo motivoReal = motivoRepository.findById(motivoId)
                        .orElseThrow(() -> new RuntimeException("El motivo seleccionado no existe"));
                nuevoReclamo.setMotivo(motivoReal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("El ID del motivo es inválido.");
            }
        } else {
            throw new RuntimeException("Debe seleccionar un motivo obligatoriamente.");
        }

        nuevoReclamo.setNumeroBoletaPedido(dto.getNumeroBoleta());

        if (dto.getFechaCompra() != null && !dto.getFechaCompra().isEmpty()) {
            nuevoReclamo.setFechaCompra(LocalDate.parse(dto.getFechaCompra()));
        }

        nuevoReclamo.setProductoImplicado(dto.getProducto());
        nuevoReclamo.setDescripcionCaso(dto.getDescripcion());
        nuevoReclamo.setFechaVencimiento(LocalDateTime.now().plusDays(21));

        // 4. Llaves foráneas por defecto
        Categoria catDefault = new Categoria();
        catDefault.setId(1);
        nuevoReclamo.setCategoria(catDefault);

        Prioridad prioDefault = new Prioridad();
        prioDefault.setId(2);
        nuevoReclamo.setPrioridad(prioDefault);

        EstadoReclamo estadoDefault = new EstadoReclamo();
        estadoDefault.setId(1);
        nuevoReclamo.setEstado(estadoDefault);

        Reclamo reclamoGuardado = reclamoRepository.save(nuevoReclamo);

        // 5. Guardar la Evidencia Y EL ARCHIVO FÍSICO
        if (archivo != null && !archivo.isEmpty()) {
            try {
                // modificado: Paso A - Definimos la ruta de la carpeta "uploads" en la raíz del
                // proyecto
                String carpetaDestino = "uploads/";
                Path directorioPath = Paths.get(carpetaDestino);

                // modificado: Paso B - Si la carpeta no existe, Spring Boot la crea físicamente
                // ahora mismo
                if (!Files.exists(directorioPath)) {
                    Files.createDirectories(directorioPath);
                }

                // modificado: Paso C - Extraemos el nombre de la imagen y construimos la ruta
                // final
                String nombreArchivo = archivo.getOriginalFilename();
                Path rutaCompleta = directorioPath.resolve(nombreArchivo);

                // modificado: Paso D - Copiamos la imagen física de la memoria RAM al disco
                // duro
                // Usamos REPLACE_EXISTING por si suben dos archivos que se llaman igual
                Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

                // Paso E - Guardamos los textos en MySQL (tu código original)
                Evidencia evidencia = new Evidencia();
                evidencia.setReclamo(reclamoGuardado);
                evidencia.setNombreArchivo(nombreArchivo);
                evidencia.setRutaArchivo("/uploads/" + nombreArchivo);
                evidencia.setTipoArchivo(archivo.getContentType());

                double sizeInMb = (double) archivo.getSize() / (1024 * 1024);
                evidencia.setTamanioMb(java.math.BigDecimal.valueOf(sizeInMb));

                evidenciaRepository.save(evidencia);

            } catch (Exception e) {
                // modificado: Capturamos cualquier error al guardar el archivo para que no
                // rompa el sistema en silencio
                throw new RuntimeException(
                        "El reclamo se guardó, pero hubo un error al guardar la imagen física: " + e.getMessage());
            }
        }

        // 2. Ve al final de tu método registrarReclamo, justo antes del "return
        // reclamoGuardado;"
        // y agrega esta línea:
        emailService.enviarCorreoRegistro(reclamoGuardado);
        return reclamoGuardado;

        // return reclamoGuardado;
    }

    // ============================================================
    // MÉTODOS DEL BACKOFFICE: ACTUALIZACIÓN POR ID
    // ============================================================
    public Reclamo actualizarEstadoReclamo(Integer id, Integer idEstado) {
        Reclamo reclamo = reclamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado"));

        EstadoReclamo nuevoEstado = estadoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        reclamo.setEstado(nuevoEstado);

        Reclamo guardado = reclamoRepository.save(reclamo);
        emailService.enviarCorreoActualizacion(guardado.getCodigoSeguimiento(),
                "El estado de tu caso ha cambiado a: " + nuevoEstado.getNombre());
        return guardado;

       // return reclamoRepository.save(reclamo);
    }

    public Reclamo actualizarPrioridadReclamo(Integer id, Integer idPrioridad) {
        Reclamo reclamo = reclamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado"));

        Prioridad nuevaPrioridad = prioridadRepository.findById(idPrioridad)
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));

        reclamo.setPrioridad(nuevaPrioridad);
        return reclamoRepository.save(reclamo);
    }

}