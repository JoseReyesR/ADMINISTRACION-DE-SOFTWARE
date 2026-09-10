import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para ngModel
import { Router } from '@angular/router'; // Para navegar entre pantallas

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], // Importamos los módulos aquí
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  // Objeto para capturar lo que el usuario escribe en la pantalla
  credenciales = {
    correo: '',
    password: ''
  };

  constructor(private router: Router) {}

  // Método que se ejecuta al darle clic a "Iniciar Sesión"
  onLogin() {
    console.log('Datos enviados:', this.credenciales);

    // Simulación temporal: si el correo es admin, vamos al panel. Si no, al registro de reclamos.
    if(this.credenciales.correo === 'admin@tottus.com') {
      this.router.navigate(['/dashboard']);
    } else {
      this.router.navigate(['/reclamo/datos']);
    }
  }

  // Método para el RF07 (Ingreso como Invitado)
  irAConsultaInvitado() {
    this.router.navigate(['/consulta']);
  }
}
