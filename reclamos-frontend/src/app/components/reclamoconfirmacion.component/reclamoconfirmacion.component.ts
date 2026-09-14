import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reclamoconfirmacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reclamoconfirmacion.component.html',
  styleUrls: ['./reclamoconfirmacion.component.css']
})
export class ReclamoconfirmacionComponent {

  // Código generado (Simulado por ahora, luego vendrá del Backend)
  codigoSeguimiento: string = 'REQ-2026-8927';
  correoCliente: string = 'maria.torres@gmail.com';

  constructor(private router: Router) {}

  irAMisCasos() {
    // Redirige al Dashboard (Panel cliente / Mis casos)
    this.router.navigate(['/mis-casos']);
  }

  salir() {
    // Redirige al inicio (Login)
    this.router.navigate(['/login']);
  }
}
