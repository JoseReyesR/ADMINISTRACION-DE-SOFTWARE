package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reclamostottus.reclamos_backend.dto.HistorialRequestDTO;
import reclamostottus.reclamos_backend.model.HistorialSeguimiento;
import reclamostottus.reclamos_backend.service.HistorialSeguimientoService;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin(origins = "http://localhost:4200")
public class HistorialSeguimientoController {

    @Autowired
    private HistorialSeguimientoService historialService;

    @PostMapping
    public ResponseEntity<HistorialSeguimiento> registrar(@RequestBody HistorialRequestDTO dto) {
        return ResponseEntity.ok(historialService.registrarHistorial(dto));
    }

    @GetMapping("/{reclamoId}/interno")
    public ResponseEntity<List<HistorialSeguimiento>> obtenerCompleto(@PathVariable Integer reclamoId) {
        return ResponseEntity.ok(historialService.obtenerHistorialCompleto(reclamoId));
    }

    @GetMapping("/{reclamoId}/publico")
    public ResponseEntity<List<HistorialSeguimiento>> obtenerPublico(@PathVariable Integer reclamoId) {
        return ResponseEntity.ok(historialService.obtenerHistorialPublico(reclamoId));
    }
}