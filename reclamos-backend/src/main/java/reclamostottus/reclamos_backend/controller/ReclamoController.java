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
import reclamostottus.reclamos_backend.repository.ReclamoRepository;
import reclamostottus.reclamos_backend.service.ReclamoService;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    @Autowired
    private EstadoReclamoRepository estadoRepository;

    @Autowired
    private PrioridadRepository prioridadRepository;

    @Autowired
    private ReclamoRepository reclamoRepository;

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

    // GET /api/reclamos/mis-casos -> Devuelve el historial del cliente logueado
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

    // ÚNICO ENDPOINT PARA OBTENER EL CASO POR ID
    @GetMapping("/admin/caso/{id}")
    public ResponseEntity<Reclamo> obtenerCasoAdmin(@PathVariable Integer id) {
        return reclamoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT /api/reclamos/admin/caso/{id}/estado/{idEstado} -> Cambiar Estado
    // (CORREGIDO A ID NUMÉRICO)
    @PutMapping("/admin/caso/{id}/estado/{idEstado}")
    public ResponseEntity<Reclamo> cambiarEstado(@PathVariable Integer id, @PathVariable Integer idEstado) {
        Reclamo reclamoActualizado = reclamoService.actualizarEstadoReclamo(id, idEstado);
        return ResponseEntity.ok(reclamoActualizado);
    }

    // PUT /api/reclamos/admin/caso/{id}/prioridad/{idPrioridad} -> Cambiar
    // Prioridad (CORREGIDO A ID NUMÉRICO)
    @PutMapping("/admin/caso/{id}/prioridad/{idPrioridad}")
    public ResponseEntity<Reclamo> cambiarPrioridad(@PathVariable Integer id, @PathVariable Integer idPrioridad) {
        Reclamo actualizado = reclamoService.actualizarPrioridadReclamo(id, idPrioridad);
        return ResponseEntity.ok(actualizado);
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

    // Endpoint para el BackOffice: Listar TODOS los reclamos
    @GetMapping("/todos")
    public ResponseEntity<List<Reclamo>> listarTodos() {
        return ResponseEntity.ok(reclamoRepository.findAll());
    }
}