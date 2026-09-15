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

  // GETTER DINÁMICO: Calcula automáticamente si el cliente está logueado
   // get esClienteRegistrado(): boolean {
   //   return localStorage.getItem('token_cliente') !== null;
   // }
  esClienteRegistrado: boolean = false; // <-- Vuelve a poner esto

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private reclamoService: ReclamoService
  ) {}
ngOnInit(): void {
    console.log('--- INICIANDO COMPONENTE RECLAMO DATOS ---');

    // 1. Revisamos el Token
    const token = typeof window !== 'undefined' ? localStorage.getItem('token_cliente') : null;
    console.log('1. Token en memoria:', token);

    if (token) {
      this.esClienteRegistrado = true;
      console.log('2. ¡Token detectado! esClienteRegistrado cambió a:', this.esClienteRegistrado);
    } else {
      console.log('2. No hay token. esClienteRegistrado se queda en:', this.esClienteRegistrado);
    }

    // 2. Revisamos los datos del cliente
    const datosGuardados = typeof window !== 'undefined' ? localStorage.getItem('cliente_datos') : null;
    console.log('3. Datos del cliente en memoria (Texto crudo):', datosGuardados);

    if (datosGuardados) {
      const usuario = JSON.parse(datosGuardados);
      console.log('4. Datos convertidos a objeto Angular:', usuario);

      this.cliente.tipoDocumento = usuario.tipoDocumento;
      this.cliente.numeroDocumento = usuario.numeroDocumento;
      this.cliente.nombres = usuario.nombres;
      this.cliente.apellidos = usuario.apellidos;
      this.cliente.correo = usuario.correo;
      this.cliente.celular = usuario.telefono;

      this.mensajeBienvenida = `¡Hola ${usuario.nombres}! Tus datos han sido precargados de tu sesión.`;
    }
    console.log('--- FIN DEL ARRANQUE ---');
  }

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


          if (err.status === 404) {
            // COMPORTAMIENTO ESPERADO: DNI no existe (Invitado).
            // Limpiamos los campos en silencio sin asustar en la consola.
            this.limpiarCampos();
          } else {
            // SOLO imprimimos si el servidor realmente falló (ej. Error 500)
            console.error('Error real de conexión con el servidor:', err);
            this.limpiarCampos();
          }
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
    // Limpiamos la sesión del cliente al retroceder
    localStorage.removeItem('token_cliente');
    localStorage.removeItem('cliente_datos');

    // Lo enviamos de regreso al login de clientes
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
  // NUEVO: Método para ir a la tabla de Mis Casos
  irAMisCasos() {
    this.router.navigate(['/mis-casos']);
  }

  irAConsulta() {

    this.router.navigate(['/consulta']);
  }
}
