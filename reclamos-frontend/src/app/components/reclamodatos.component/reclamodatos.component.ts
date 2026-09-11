import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reclamodatos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reclamodatos.component.html',
  styleUrls: ['./reclamodatos.component.css']
})
export class ReclamodatosComponent {
  // Objeto para capturar los datos del usuario (RN-01 y RN-03)
  cliente = {
    tipoDocumento: 'DNI',
    numeroDocumento: '',
    nombres: '',
    apellidos: '',
    correo: '',
    celular: ''
  };

  constructor(private router: Router) {}

  // Método para avanzar al siguiente paso
  siguientePaso() {
    console.log('Datos personales capturados:', this.cliente);

    // CAMBIO APLICADO: Navegamos hacia el paso 2 (Confirmación)
    this.router.navigate(['/reclamo/evidencia']);
  }

  // Método para regresar al login/inicio
  volver() {
    this.router.navigate(['/login']);
  }
}
