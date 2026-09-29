import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';

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

  constructor(private authService: AuthService, private usuarioService: UsuarioService, private router: Router,private cdr: ChangeDetectorRef) {}



iniciarSesion() {
    this.authService.cerrarSesion(); // Limpiamos sesiones previas
    // Limpiamos también las variables específicas del cliente
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');

    this.authService.login(this.credenciales).subscribe({
      next: () => {
        this.mensajeError = '';
        const rol = this.authService.obtenerRol();

        // Validamos que sea estrictamente un Cliente
        if (rol === 'ROLE_CLIENTE' || rol === 'Cliente') {

          // 1. GUARDAMOS LA LLAVE ESPECÍFICA DEL CLIENTE
          const tokenReal = this.authService.obtenerToken();
          if (tokenReal) {
             localStorage.setItem('token_cliente', tokenReal);
          }

          // 2. GUARDAMOS SUS DATOS BÁSICOS EN MEMORIA
          // Guardamos el correo que usó para loguearse. Si falta el DNI,
          // el formulario del Paso 1 lo pedirá y buscará el resto.
          this.usuarioService.buscarPorCorreo(this.credenciales.correo).subscribe({
          next: (usuario) => {
            if (usuario) {
              localStorage.setItem('cliente_datos', JSON.stringify({
                tipoDocumento: usuario.tipoDocumento || 'DNI',
                numeroDocumento: usuario.numeroDocumento || this.credenciales.correo,
                nombres: usuario.nombres || 'Cliente Registrado',
                apellidos: usuario.apellidos || '',
                correo: usuario.correo || this.credenciales.correo,
                telefono: usuario.telefono || ''
              }));
            } else {
              console.warn('No se encontró información del usuario.');
            }
            // Redirigimos ÚNICAMENTE cuando la data ya está guardada en localStorage
            this.router.navigate(['/reclamo/datos']);
          },
          error: (err) => {
            console.error('Error al buscar el usuario:', err);
            // Redirigimos de todos modos o manejamos un aviso previo
            this.router.navigate(['/reclamo/datos']);
          }
        });

        } else {
          this.authService.cerrarSesion();
          this.mensajeError = 'Esta cuenta pertenece al BackOffice. Utilice el acceso administrativo.';
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        if (error.status === 401 || error.status === 403) {
          this.mensajeError = 'Credenciales incorrectas. Verifique su correo y contraseña.';
          this.cdr.detectChanges();
        } else {
          this.mensajeError = 'Error de conexión con el servidor.';
          this.cdr.detectChanges();
        }
      }
    });
  }

  continuarComoInvitado() {
    this.router.navigate(['/reclamo/datos']);
  }
}
