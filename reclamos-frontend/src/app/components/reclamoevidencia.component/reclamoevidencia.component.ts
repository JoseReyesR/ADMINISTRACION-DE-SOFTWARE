import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';
import { CatalogoService } from '../../services/catalogo.service';

@Component({
  selector: 'app-reclamoevidencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reclamoevidencia.component.html',
  styleUrls: ['./reclamoevidencia.component.css']
})
export class ReclamoevidenciaComponent implements OnInit {
  incidente = {
    tipoSolicitud: 'Reclamo',
    canalCompra: 'Tienda Física',
    tienda: '',
    numeroBoleta: '',
    fechaCompra: '',
    categoria: '',
    motivo: '',
    producto: '',
    descripcion: ''
  };

  listaTiendas: any[] = [];
  listaMotivos: any[] = [];
  listaCategorias: any[] = [];

  archivoSeleccionado: File | null = null;
  mensajeError: string = '';
  esClienteRegistrado: boolean = false;

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private catalogoService: CatalogoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (typeof window !== 'undefined' && localStorage.getItem('token_cliente')) {
      this.esClienteRegistrado = true;
    }

    this.catalogoService.obtenerTiendas().subscribe(data => {
      this.listaTiendas = data;
      this.cdr.detectChanges();
    });

    this.catalogoService.obtenerMotivos().subscribe(data => {
      this.listaMotivos = data;
      this.cdr.detectChanges();
    });

    this.catalogoService.obtenerCategorias().subscribe(data => {
      this.listaCategorias = data;
      this.cdr.detectChanges();
    });
  }

  onArchivoSeleccionado(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const tamanioMB = file.size / (1024 * 1024);
      if (tamanioMB > 10) {
        this.mensajeError = 'El archivo supera los 10 MB permitidos.';
        this.archivoSeleccionado = null;
      } else {
        this.archivoSeleccionado = file;
        this.mensajeError = '';
      }
      this.cdr.detectChanges();
    }
  }

  siguientePaso() {
    // --- INICIO DE CORRECCIÓN ---
    // Forzamos a que los IDs se envíen estrictamente como Números enteros (Integer)
    // y añadimos 'categoriaId' como respaldo por si tu DTO en Java lo exige con ese nombre exacto.
    const incidenteFormateado = {
      ...this.incidente,
      tienda: Number(this.incidente.tienda),
      motivo: Number(this.incidente.motivo),
      categoria: Number(this.incidente.categoria),
      categoriaId: Number(this.incidente.categoria)
    };
    // --- FIN DE CORRECCIÓN ---

    this.reclamoService.enviarReclamoTotal(incidenteFormateado, this.archivoSeleccionado).subscribe({
      next: (respuesta) => {
       this.router.navigate(['/reclamo/confirmacion'], {
         state: {
           codigo: respuesta.codigoSeguimiento,
           correo: this.reclamoService.obtenerCorreoCliente()
         }
        });
      },
      error: (error) => {
        console.error('Error del servidor:', error);
        this.mensajeError = 'Hubo un error de conexión al guardar el reclamo.';
        this.cdr.detectChanges();
      }
    });
  }

  volver() { this.router.navigate(['/reclamo/datos']); }
  nuevoReclamo() { this.router.navigate(['/reclamo/datos']); }
  irAMisCasos() { this.router.navigate(['/mis-casos']); }
  irAConsulta() { this.router.navigate(['/consulta']); }
  salir() {
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');
    this.router.navigate(['/ingresar']);
  }
}
