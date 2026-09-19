import { Component, OnInit } from '@angular/core';
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
export class ReclamodatosComponent implements OnInit {
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
  esClienteRegistrado: boolean = false;

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private reclamoService: ReclamoService
  ) {}

  ngOnInit(): void {
    console.log('--- INICIANDO COMPONENTE RECLAMO DATOS ---');
    const token = typeof window !== 'undefined' ? localStorage.getItem('token_cliente') : null;

    if (token) {
      this.esClienteRegistrado = true;
    }

    const datosGuardados = typeof window !== 'undefined' ? localStorage.getItem('cliente_datos') : null;

    if (datosGuardados) {
      const usuario = JSON.parse(datosGuardados);
      this.cliente.tipoDocumento = usuario.tipoDocumento;
      this.cliente.numeroDocumento = usuario.numeroDocumento;
      this.cliente.nombres = usuario.nombres;
      this.cliente.apellidos = usuario.apellidos;
      this.cliente.correo = usuario.correo;
      this.cliente.celular = usuario.telefono;

      this.mensajeBienvenida = `¡Hola ${usuario.nombres}! Tus datos han sido precargados de tu sesión.`;
    }
  }

  // =========================================================
  // NUEVO: GETTERS PARA VALIDACIONES REGEX DINÁMICAS
  // =========================================================

  // Define el patrón exacto según el documento seleccionado
  get documentoPattern(): string {
    if (this.cliente.tipoDocumento === 'DNI') return '^[0-9]{8}$'; // Exactamente 8 números
    if (this.cliente.tipoDocumento === 'CE') return '^[0-9]{9}$'; // Exactamente 9 números
    if (this.cliente.tipoDocumento === 'PASAPORTE') return '^[A-Za-z]{1}[0-9]{8}$'; // 1 letra y 8 números
    return '.*';
  }

  // Define el mensaje de error según el documento seleccionado
  get mensajeErrorDocumento(): string {
    if (this.cliente.tipoDocumento === 'DNI') return 'El DNI debe tener exactamente 8 números.';
    if (this.cliente.tipoDocumento === 'CE') return 'El Carnet de Extranjería debe tener exactamente 9 números.';
    if (this.cliente.tipoDocumento === 'PASAPORTE') return 'El Pasaporte debe tener 1 letra seguida de 8 números.';
    return 'Documento inválido.';
  }

  // =========================================================

  buscarCliente() {
    // Validamos que cumpla el patrón de 8 dígitos numéricos antes de buscar en BD
    const esDniValido = new RegExp('^[0-9]{8}$').test(this.cliente.numeroDocumento);

    if (this.cliente.tipoDocumento === 'DNI' && esDniValido) {
      this.usuarioService.buscarPorDocumento(this.cliente.numeroDocumento).subscribe({
        next: (datos) => {
          if (datos.encontrado) {
            this.cliente.nombres = datos.nombres;
            this.cliente.apellidos = datos.apellidos;
            this.cliente.correo = datos.correo;
            this.cliente.celular = datos.telefono;
            this.mensajeBienvenida = `¡Hola ${datos.nombres}! Tus datos han sido cargados.`;
          } else {
            this.limpiarCampos();
          }
        },
        error: (err) => {
          if (err.status === 404) {
            this.limpiarCampos();
          } else {
            console.error('Error real de conexión con el servidor:', err);
            this.limpiarCampos();
          }
        }
      });
    } else {
      this.mensajeBienvenida = '';
    }
  }

  limpiarCampos() {
    this.mensajeBienvenida = '';
    this.cliente.nombres = '';
    this.cliente.apellidos = '';
    this.cliente.correo = '';
    this.cliente.celular = '';
  }

  siguientePaso() {
    // NUEVO: Doble validación manual al hacer clic en "Continuar"
    if (!new RegExp(this.documentoPattern).test(this.cliente.numeroDocumento)) {
      alert(`Error: ${this.mensajeErrorDocumento}`);
      return;
    }
    if (!this.cliente.nombres.trim() || !this.cliente.apellidos.trim()) {
      alert('Error: Los nombres y apellidos no pueden estar vacíos ni contener solo espacios.');
      return;
    }
    if (!new RegExp('^[0-9]{9}$').test(this.cliente.celular)) {
      alert('Error: El celular debe contener exactamente 9 dígitos numéricos.');
      return;
    }
    if (!new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$').test(this.cliente.correo)) {
      alert('Error: Debe ingresar un correo electrónico válido (ejemplo@correo.com).');
      return;
    }

    // Si todo está perfecto, avanza
    this.reclamoService.guardarDatosCliente(this.cliente);
    this.router.navigate(['/reclamo/evidencia']);
  }

  volver() {
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');
    this.router.navigate(['/ingresar']);
  }

  salir() {
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');
    this.router.navigate(['/ingresar']);
  }

  nuevoReclamo() {
    this.cliente = { tipoDocumento: 'DNI', numeroDocumento: '', nombres: '', apellidos: '', correo: '', celular: '' };
    this.limpiarCampos();
  }

  irAMisCasos() {
    this.router.navigate(['/mis-casos']);
  }

  irAConsulta() {
    this.router.navigate(['/consulta']);
  }
}
