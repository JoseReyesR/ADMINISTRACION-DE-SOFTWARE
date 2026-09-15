import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reclamoconfirmacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reclamoconfirmacion.component.html',
  styleUrls: ['./reclamoconfirmacion.component.css']
})
export class ReclamoconfirmacionComponent implements OnInit {

  // Variables dinámicas
  codigoSeguimiento: string = 'Generando...';
  correoCliente: string = '';

  // Variable para controlar si mostramos "Mis Casos"
  esClienteRegistrado: boolean = false;

  constructor(private router: Router) {
    // 1. Rescatamos los datos reales que envía el Paso 2 por la ruta
    const navegacion = this.router.getCurrentNavigation();
    if (navegacion?.extras.state) {
      this.codigoSeguimiento = navegacion.extras.state['codigo'];
      this.correoCliente = navegacion.extras.state['correo'];
    }
  }

  ngOnInit(): void {
    // 2. Verificamos si es un usuario logueado o un invitado
    if (typeof window !== 'undefined' && localStorage.getItem('token_cliente')) {
      this.esClienteRegistrado = true;

      // Si por alguna razón el correo no llegó en el state, lo rescatamos de la memoria
      if (!this.correoCliente) {
        const datosGuardados = localStorage.getItem('cliente_datos');
        if (datosGuardados) {
          this.correoCliente = JSON.parse(datosGuardados).correo;
        }
      }
    }
  }

  irAMisCasos() {
    this.router.navigate(['/mis-casos']);
  }

  salir() {
    // Limpiamos la sesión correctamente al salir del flujo
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');
    this.router.navigate(['/ingresar']);
  }
}
