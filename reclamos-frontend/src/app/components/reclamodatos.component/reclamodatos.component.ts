import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service'; // Importamos el servicio

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

  // Inyectamos el servicio en el constructor
  constructor(private router: Router, private reclamoService: ReclamoService) {}

  // Método para avanzar al siguiente paso
  siguientePaso() {
    // Guardamos en el servicio en lugar de solo imprimir en consola
    this.reclamoService.guardarDatosCliente(this.cliente);
    this.router.navigate(['/reclamo/evidencia']);
  }

  // Método para regresar al login/inicio
  volver() {
    this.router.navigate(['/login']);
  }
}
