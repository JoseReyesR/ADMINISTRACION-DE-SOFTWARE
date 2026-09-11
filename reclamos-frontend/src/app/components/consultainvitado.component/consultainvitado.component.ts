import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-consulta-invitado',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consultainvitado.component.html',
  styleUrls: ['./consultainvitado.component.css']
})
export class ConsultainvitadoComponent {
  consulta = {
    tipoDocumento: 'DNI',
    numeroDocumento: '',
    codigoSeguimiento: ''
  };

  resultado: any = null;
  buscado: boolean = false;

  constructor(private router: Router) {}

  consultarCaso() {
    this.buscado = true;
    // Simulación de búsqueda para la regla de negocio
    if (this.consulta.codigoSeguimiento === 'REQ-2026-8927') {
      this.resultado = {
        codigo: 'REQ-2026-8927',
        estado: 'En Proceso',
        fecha: '09/09/2026',
        motivo: 'Cobro equivocado en caja'
      };
    } else {
      this.resultado = null;
    }
  }

  volver() {
    this.router.navigate(['/login']);
  }
}
