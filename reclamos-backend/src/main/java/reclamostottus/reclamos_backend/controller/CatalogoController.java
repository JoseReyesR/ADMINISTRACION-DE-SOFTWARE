package reclamostottus.reclamos_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reclamostottus.reclamos_backend.model.Tienda;
import reclamostottus.reclamos_backend.model.CatalogoMotivo;
import reclamostottus.reclamos_backend.model.Categoria; // <-- Nueva importación
import reclamostottus.reclamos_backend.repository.TiendaRepository;
import reclamostottus.reclamos_backend.repository.CatalogoMotivoRepository;
import reclamostottus.reclamos_backend.repository.CategoriaRepository; // <-- Nueva importación

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin(origins = "http://localhost:4200") // Fundamental para que Angular no sea bloqueado
public class CatalogoController {

    @Autowired
    private TiendaRepository tiendaRepository;

    @Autowired
    private CatalogoMotivoRepository motivoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository; // <-- Inyección del nuevo repositorio

    // Endpoint 1: Devuelve la lista de tiendas
    @GetMapping("/tiendas")
    public List<Tienda> listarTiendas() {
        return tiendaRepository.findAll();
    }

    // Endpoint 2: Devuelve la lista de motivos (Reclamos y Quejas)
    @GetMapping("/motivos")
    public List<CatalogoMotivo> listarMotivos() {
        return motivoRepository.findAll();
    }

    // Endpoint 3: Devuelve la lista de categorías
    @GetMapping("/categorias")
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
}