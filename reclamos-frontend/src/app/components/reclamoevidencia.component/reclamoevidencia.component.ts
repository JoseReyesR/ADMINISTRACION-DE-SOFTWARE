import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';
import { CatalogoService } from '../../services/catalogo.service'; // <-- NUEVO SERVICIO

@Component({
  selector: 'app-reclamoevidencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reclamoevidencia.component.html',
  styleUrls: ['./reclamoevidencia.component.css']
})
export class ReclamoevidenciaComponent implements OnInit {
  // Datos combinados del incidente
  incidente = {
    tipoSolicitud: 'Reclamo',
    canalCompra: 'Tienda Física',
    tienda: '', // Ahora guardará el ID de la tienda
    numeroBoleta: '',
    fechaCompra: '',
    motivo: '', // Ahora guardará el ID del motivo
    producto: '',
    descripcion: ''
  };

  // Arreglos para guardar los datos de la Base de Datos
  listaTiendas: any[] = [];
  listaMotivos: any[] = [];

  // Variables para la evidencia
  archivoSeleccionado: File | null = null;
  mensajeError: string = '';
  esClienteRegistrado: boolean = false;

  // Inyectamos ambos servicios
  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private catalogoService: CatalogoService
  ) {}

  ngOnInit(): void {
    if (typeof window !== 'undefined' && localStorage.getItem('token_cliente')) {
      this.esClienteRegistrado = true;
    }

    // DESCARGAMOS LOS CATÁLOGOS AL ABRIR LA PANTALLA
    this.catalogoService.obtenerTiendas().subscribe(data => this.listaTiendas = data);
    this.catalogoService.obtenerMotivos().subscribe(data => this.listaMotivos = data);
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
    }
  }

  siguientePaso() {
    this.reclamoService.enviarReclamoTotal(this.incidente, this.archivoSeleccionado).subscribe({
      next: (respuesta) => {
        this.router.navigate(['/reclamo/confirmacion']);
      },
      error: (error) => {
        console.error('Error del servidor:', error);
        this.mensajeError = 'Hubo un error de conexión al guardar el reclamo.';
      }
    });
  }

  volver() {
    this.router.navigate(['/reclamo/datos']);
  }

  nuevoReclamo() {
    this.router.navigate(['/reclamo/datos']);
  }

  irAMisCasos() {
    this.router.navigate(['/mis-casos']);
  }

  irAConsulta() {
    this.router.navigate(['/consulta']);
  }

  salir() {
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');
    this.router.navigate(['/ingresar']);
  }
}
