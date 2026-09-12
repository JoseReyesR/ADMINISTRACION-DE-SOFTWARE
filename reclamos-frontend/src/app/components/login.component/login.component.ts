import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para ngModel
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credenciales = {
    correo: '',
    password: ''
  };

  mensajeError: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  iniciarSesion() {
    this.authService.login(this.credenciales).subscribe({
      next: (respuesta) => {
        this.mensajeError = '';

        // 1. Decodificamos el token para extraer el rol del usuario
        const rol = this.authService.obtenerRol();

        // 2. Evaluamos el nivel de privilegios
        if (rol === 'ROLE_ADMIN' || rol === 'ROLE_REGISTRADOR' || rol === 'ROLE_TECNICO') {
          // Es un trabajador: lo dejamos pasar al BackOffice
          this.router.navigate(['/dashboard']);
        } else {
          // Es un cliente (o rol desconocido): bloqueamos su ingreso administrativo
          this.authService.cerrarSesion();
          this.mensajeError = 'Acceso denegado. Por favor, utilice el portal de clientes para ingresar.';
        }
      },
      error: (error) => {
        console.error('Error de autenticación:', error);
        if (error.status === 401 || error.status === 403) {
          this.mensajeError = 'Credenciales incorrectas. Verifique su correo y contraseña.';
        } else {
          this.mensajeError = 'Error de conexión con el servidor.';
        }
      }
    });
  }
}
