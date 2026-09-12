package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.service.ReclamoService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    // POST /api/reclamos -> Recibe el formulario de Angular y lo guarda
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reclamo> crearReclamo(
            @RequestPart("reclamo") ReclamoRequestDTO dto,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        Reclamo reclamoGuardado = reclamoService.registrarReclamo(dto, archivo);
        return ResponseEntity.ok(reclamoGuardado);
    }

    // GET /api/reclamos -> Alimenta la tabla del Dashboard (Mis Casos)
    @GetMapping
    public ResponseEntity<List<Reclamo>> listarReclamos() {
        List<Reclamo> reclamos = reclamoService.listarTodos();
        return ResponseEntity.ok(reclamos);
    }

    // GET /api/reclamos/seguimiento/{codigo} -> Alimenta la vista "Seguimiento
    // Invitado"
    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<Reclamo> seguimientoInvitado(@PathVariable String codigo) {
        Optional<Reclamo> reclamo = reclamoService.buscarPorCodigo(codigo);
        return reclamo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}