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
        // El token ya se guardó en el localStorage gracias al AuthService
        this.mensajeError = '';
        this.router.navigate(['/dashboard']); // Redirigimos al área segura
      },
      error: (error) => {
        console.error('Error de autenticación:', error);
        // Validamos si el error es por credenciales incorrectas (401/403)
        if (error.status === 401 || error.status === 403) {
          this.mensajeError = 'Credenciales incorrectas. Verifique su correo y contraseña.';
        } else {
          this.mensajeError = 'Error de conexión con el servidor.';
        }
      }
    });
  }
}
