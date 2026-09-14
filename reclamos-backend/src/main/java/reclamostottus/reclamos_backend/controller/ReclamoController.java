package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.EstadoReclamo;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.repository.EstadoReclamoRepository;
import reclamostottus.reclamos_backend.service.ReclamoService;

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
}