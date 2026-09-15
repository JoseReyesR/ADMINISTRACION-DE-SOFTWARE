import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

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
    tienda: '',
    numeroBoleta: '',
    fechaCompra: '',
    motivo: '',
    producto: '',
    descripcion: ''
  };

  // Variables para la evidencia
  archivoSeleccionado: File | null = null;
  mensajeError: string = '';

  // Variable para controlar la vista del cliente
  esClienteRegistrado: boolean = false;

  // Inyectamos el servicio
  constructor(private router: Router, private reclamoService: ReclamoService) {}

  ngOnInit(): void {
    // Verificamos si el cliente está logueado para mostrar "Mis Casos"
    if (typeof window !== 'undefined' && localStorage.getItem('token_cliente')) {
      this.esClienteRegistrado = true;
    }
  }

  // Validación de 10 MB para la evidencia
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

  // --- NUEVAS FUNCIONES DE NAVEGACIÓN SUPERIOR ---

  nuevoReclamo() {
    // Redirige al paso 1 para limpiar todo e iniciar de nuevo
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
