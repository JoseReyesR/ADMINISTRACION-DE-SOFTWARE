import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-reclamodatos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reclamodatos.component.html',
  styleUrls: ['./reclamodatos.component.css']
})
export class ReclamodatosComponent {

  // Objeto para capturar los datos del usuario (RN-01 y RN-03)
  cliente = {
    tipoDocumento: 'DNI',
    numeroDocumento: '',
    nombres: '',
    apellidos: '',
    correo: '',
    celular: ''
  };

  mensajeBienvenida: string = '';

  // Constructor unificado con todos los servicios inyectados
  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private reclamoService: ReclamoService
  ) {}

  buscarCliente() {
    // Solo buscamos si es DNI y tiene exactamente 8 dígitos
    if (this.cliente.tipoDocumento === 'DNI' && this.cliente.numeroDocumento.length === 8) {
      this.usuarioService.buscarPorDocumento(this.cliente.numeroDocumento).subscribe({
        next: (datos) => {
          // Evaluamos la variable booleana que nos envía Spring Boot
          if (datos.encontrado) {
            // Cliente encontrado: Autocompletamos los campos
            this.cliente.nombres = datos.nombres;
            this.cliente.apellidos = datos.apellidos;
            this.cliente.correo = datos.correo;
            this.cliente.celular = datos.telefono;
            this.mensajeBienvenida = `¡Hola ${datos.nombres}! Tus datos han sido cargados.`;
          } else {
            // No encontrado en BD: Pasa a modo Invitado limpiando los campos
            this.limpiarCampos();
          }
        },
        error: (err) => {
          console.error('Error de conexión:', err);
          this.limpiarCampos();
        }
      });
    } else {
      this.mensajeBienvenida = '';
    }
  }

  // Método de apoyo para mantener el código ordenado
  limpiarCampos() {
    this.mensajeBienvenida = '';
    this.cliente.nombres = '';
    this.cliente.apellidos = '';
    this.cliente.correo = '';
    this.cliente.celular = '';
  }

  // Método para avanzar al siguiente paso
  siguientePaso() {
    // Guardamos en el servicio en lugar de solo imprimir en consola
    this.reclamoService.guardarDatosCliente(this.cliente);
    this.router.navigate(['/reclamo/evidencia']);
  }

  // Método para regresar al login/inicio
  volver() {
    this.router.navigate(['/login']);
  }

  // NUEVO: Método para ir a la tabla de Mis Casos
  irAMisCasos() {
    this.router.navigate(['/mis-casos']);
  }


}
