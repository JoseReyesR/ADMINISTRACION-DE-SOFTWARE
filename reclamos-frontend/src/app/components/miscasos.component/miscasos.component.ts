import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-miscasos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './miscasos.component.html',
  styleUrls: ['./miscasos.component.css']
})
export class MiscasosComponent {

  // Mantenemos los datos simulados temporalmente para no perder tu diseño
  reclamos = [
    { codigo: 'REQ-2026-8927', fecha: '9/9/2026', motivo: '[Queja] Tienda - Información inc...', estado: 'En Proceso' },
    { codigo: 'REQ-2026-3113', fecha: '6/9/2026', motivo: '[Queja] Tienda - Demora excesiv...', estado: 'En Proceso' },
    { codigo: 'REQ-2025-245', fecha: '27/11/2025', motivo: '[Reclamo] Tienda - Cobro equivo...', estado: 'En Proceso' },
    { codigo: 'REQ-2025-101', fecha: '01/10/2025', motivo: '[Reclamo] Web - Falta producto', estado: 'Vencido' },
    { codigo: 'REQ-2025-402', fecha: '15/11/2025', motivo: '[Reclamo] Tienda - Producto ven...', estado: 'Resuelto' },
    { codigo: 'REQ-2025-750', fecha: '20/11/2025', motivo: '[Queja] Web - Delivery no llegó', estado: 'En Análisis' },
    { codigo: 'REQ-2025-889', fecha: '26/11/2025', motivo: '[Reclamo] Tienda - Cobro equivo...', estado: 'En Proceso' }
  ];

  constructor(private router: Router) {}

  nuevoReclamo() {
    this.router.navigate(['/reclamo/datos']);
  }

  verDetalle(codigo: string) {
    console.log('Viendo detalle del caso:', codigo);
    // Lógica futura para ver detalles del reclamo
  }

  salir() {
    // Redirige al login de clientes, no al del BackOffice
    this.router.navigate(['/ingresar']);
  }
}
