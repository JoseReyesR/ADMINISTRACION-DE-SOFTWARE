import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ingreso-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ingreso-cliente.component.html',
  styleUrls: ['./ingreso-cliente.component.css']
})
export class IngresoClienteComponent {
  credenciales = {
    correo: '',
    password: ''
  };

  mensajeError: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  iniciarSesion() {
    this.authService.cerrarSesion(); // Limpiamos sesiones previas

    this.authService.login(this.credenciales).subscribe({
      next: () => {
        this.mensajeError = '';
        const rol = this.authService.obtenerRol();

        // Validamos que sea estrictamente un Cliente
        if (rol === 'ROLE_CLIENTE' || rol === 'Cliente') {
          // Si implementas la vista de "Mis Casos", la ruta iría aquí
          this.router.navigate(['/reclamo/datos']);
        } else {
          this.authService.cerrarSesion();
          this.mensajeError = 'Esta cuenta pertenece al BackOffice. Utilice el acceso administrativo.';
        }
      },
      error: (error) => {
        if (error.status === 401 || error.status === 403) {
          this.mensajeError = 'Credenciales incorrectas. Verifique su correo y contraseña.';
        } else {
          this.mensajeError = 'Error de conexión con el servidor.';
        }
      }
    });
  }

  continuarComoInvitado() {
    this.router.navigate(['/reclamo/datos']);
  }
}
