import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reclamoevidencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reclamoevidencia.component.html',
  styleUrls: ['./reclamoevidencia.component.css']
})
export class ReclamoevidenciaComponent {
  // Datos combinados del incidente
  incidente = {
    tipoSolicitud: 'Reclamo',
    canalCompra: 'Tienda Física', // Nuevo
    tienda: '',
    numeroBoleta: '',
    motivo: '',                   // Nuevo
    producto: '',                 // Nuevo
    descripcion: ''
  };

  // Variables para la evidencia
  archivoSeleccionado: File | null = null;
  mensajeError: string = '';

  constructor(private router: Router) {}

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
    console.log('Incidente:', this.incidente, 'Archivo:', this.archivoSeleccionado?.name);
    // Navegamos al Paso 3 (Confirmación Final)
    this.router.navigate(['/reclamo/confirmacion']);
  }

  volver() {
    this.router.navigate(['/reclamo/datos']);
  }
}
