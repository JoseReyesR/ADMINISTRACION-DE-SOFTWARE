import { Component, ChangeDetectorRef } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  credenciales = { correo: '', password: '' };
  mensajeError: string = '';

  constructor(private authService: AuthService, private router: Router, private cdr: ChangeDetectorRef) {}

  iniciarSesion() {
    this.authService.cerrarSesion(); // Limpiamos basuras previas

    this.authService.login(this.credenciales).subscribe({
      next: () => {
        this.mensajeError = '';
        const rol = this.authService.obtenerRol();

        if (rol === 'ROLE_ADMIN' || rol === 'ROLE_TECNICO' || rol === 'Soporte') {
          this.router.navigate(['/dashboard']);
        } else {
          this.mensajeError = 'Rol no autorizado para el BackOffice.';
          this.cdr.detectChanges();
          this.authService.cerrarSesion();

        }
      },
      error: () => {
        this.mensajeError = 'Credenciales incorrectas. Verifique su correo y contraseña.';
        this.cdr.detectChanges();
      }
    });
  }
}
