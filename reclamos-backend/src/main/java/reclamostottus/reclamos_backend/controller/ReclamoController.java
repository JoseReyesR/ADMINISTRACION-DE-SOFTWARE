package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.EstadoReclamo;
import reclamostottus.reclamos_backend.model.Prioridad;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.repository.EstadoReclamoRepository;
import reclamostottus.reclamos_backend.repository.PrioridadRepository;
import reclamostottus.reclamos_backend.service.ReclamoService;
import org.springframework.security.core.Authentication; // Asegúrate de importar esto

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    // INYECCIÓN CLAVE: Esto evita el Error 500 al buscar los estados
    @Autowired
    private EstadoReclamoRepository estadoRepository;

    @Autowired
    private PrioridadRepository prioridadRepository;

    // POST /api/reclamos -> Recibe el formulario del Cliente
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reclamo> crearReclamo(
            @RequestPart("reclamo") ReclamoRequestDTO dto,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        Reclamo reclamoGuardado = reclamoService.registrarReclamo(dto, archivo);
        return ResponseEntity.ok(reclamoGuardado);
    }

    // GET /api/reclamos -> Alimenta la tabla "Mis Casos" del cliente
    @GetMapping
    public ResponseEntity<List<Reclamo>> listarReclamos() {
        List<Reclamo> reclamos = reclamoService.listarTodos();
        return ResponseEntity.ok(reclamos);
    }

    // GET /api/reclamos/seguimiento/{codigo}/{dni} -> Seguimiento Invitado
    @GetMapping("/seguimiento/{codigo}/{dni}")
    public ResponseEntity<Reclamo> seguimientoInvitado(@PathVariable String codigo, @PathVariable String dni) {
        Optional<Reclamo> reclamo = reclamoService.seguimientoSeguroInvitado(codigo, dni);
        return reclamo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // NUEVO: GET /api/reclamos/mis-casos -> Devuelve el historial del cliente
    // logueado
    @GetMapping("/mis-casos")
    public ResponseEntity<List<Reclamo>> listarMisCasos(Authentication authentication) {
        // Extraemos el correo directamente del Token JWT por seguridad
        String correoCliente = authentication.getName();
        // --- INICIO DE RASTREO ---
        System.out.println("\n=== DEBUG PANEL MIS CASOS ===");
        System.out.println("1. Correo extraído del Token JWT: [" + correoCliente + "]");

        List<Reclamo> historial = reclamoService.obtenerMisCasos(correoCliente);

        System.out.println("2. Cantidad de reclamos encontrados en MySQL: " + historial.size());
        System.out.println("===============================\n");
        // --- FIN DE RASTREO ---

        return ResponseEntity.ok(historial);
    }

    // ============================================================
    // ENDPOINTS DEL BACKOFFICE (ADMINISTRADORES)
    // ============================================================

    // GET /api/reclamos/admin/todos -> Dashboard del BackOffice
    @GetMapping("/admin/todos")
    public ResponseEntity<List<Reclamo>> listarCasosAdministrativos() {
        List<Reclamo> todosLosCasos = reclamoService.listarTodos();
        return ResponseEntity.ok(todosLosCasos);
    }

    // GET /api/reclamos/admin/caso/{codigo} -> Ver Detalle Administrativo
    @GetMapping("/admin/caso/{codigo}")
    public ResponseEntity<Reclamo> obtenerCasoAdmin(@PathVariable String codigo) {
        Optional<Reclamo> reclamo = reclamoService.buscarPorCodigo(codigo);
        return reclamo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT /api/reclamos/admin/caso/{codigo}/estado/{idEstado} -> Cambiar Estado
    @PutMapping("/admin/caso/{codigo}/estado/{idEstado}")
    public ResponseEntity<Reclamo> cambiarEstado(@PathVariable String codigo, @PathVariable Integer idEstado) {
        Reclamo reclamoActualizado = reclamoService.actualizarEstadoReclamo(codigo, idEstado);
        return ResponseEntity.ok(reclamoActualizado);
    }

    // GET /api/reclamos/admin/estados -> El Catálogo para tu Menú Desplegable
    @GetMapping("/admin/estados")
    public ResponseEntity<List<EstadoReclamo>> listarEstados() {
        List<EstadoReclamo> estados = estadoRepository.findAll();
        return ResponseEntity.ok(estados);
    }

    @GetMapping("/admin/prioridades")
    public ResponseEntity<List<Prioridad>> listarPrioridades() {
        return ResponseEntity.ok(prioridadRepository.findAll());
    }

    @PutMapping("/admin/caso/{codigo}/prioridad/{idPrioridad}")
    public ResponseEntity<Reclamo> cambiarPrioridad(@PathVariable String codigo, @PathVariable Integer idPrioridad) {
        Reclamo actualizado = reclamoService.actualizarPrioridadReclamo(codigo, idPrioridad);
        return ResponseEntity.ok(actualizado);
    }
}