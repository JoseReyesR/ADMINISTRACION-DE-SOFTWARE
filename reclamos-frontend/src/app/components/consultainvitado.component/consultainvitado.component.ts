import { Component, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

@Component({
  selector: 'app-consulta-invitado',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consultainvitado.component.html',
  styleUrls: ['./consultainvitado.component.css']
})
export class ConsultainvitadoComponent {
  consulta = {
    tipoDocumento: 'DNI',
    numeroDocumento: '',
    codigoSeguimiento: ''
  };

  resultado: any = null;
  buscado: boolean = false;

  mostrarModal: boolean = false;
  historial: any[] = [];

  mostrarModalDetalles: boolean = false;
  casoSeleccionado: any = null;

  mostrarModalImagen: boolean = false;
  imagenSeleccionada: string = '';
  private backendUrl = 'http://localhost:8080';

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  // =========================================================
  // NUEVO: GETTERS PARA VALIDACIONES REGEX DINÁMICAS
  // =========================================================
  get documentoPattern(): string {
    if (this.consulta.tipoDocumento === 'DNI') return '^[0-9]{8}$';
    if (this.consulta.tipoDocumento === 'CE') return '^[0-9]{9}$';
    if (this.consulta.tipoDocumento === 'PASAPORTE') return '^[A-Za-z]{1}[0-9]{8}$';
    return '.*';
  }

  get mensajeErrorDocumento(): string {
    if (this.consulta.tipoDocumento === 'DNI') return 'El DNI debe tener exactamente 8 números.';
    if (this.consulta.tipoDocumento === 'CE') return 'El Carnet de Extranjería debe tener exactamente 9 números.';
    if (this.consulta.tipoDocumento === 'PASAPORTE') return 'El Pasaporte debe tener 1 letra seguida de 8 números.';
    return 'Documento inválido.';
  }
  // =========================================================

  consultarCaso() {
    // Doble validación de seguridad por si vulneran el HTML
    if (!new RegExp(this.documentoPattern).test(this.consulta.numeroDocumento)) {
      alert(`Error: ${this.mensajeErrorDocumento}`);
      return;
    }
    if (!this.consulta.codigoSeguimiento.trim()) {
      alert('Error: El código de seguimiento es obligatorio.');
      return;
    }

    this.buscado = false;
    this.resultado = null;

    this.reclamoService.consultarSeguimiento(this.consulta.codigoSeguimiento, this.consulta.numeroDocumento)
      .subscribe({
        next: (datosBackend) => {
          this.ngZone.run(() => {
            this.resultado = {
              id: datosBackend.id,
              codigo: datosBackend.codigoSeguimiento,
              estado: datosBackend.estado ? datosBackend.estado.nombre : 'Ingresado',
              fecha: datosBackend.fechaRegistro || 'Reciente',
              motivo: `[${datosBackend.tipoSolicitud}] - ${datosBackend.productoImplicado || 'General'}`,
              detalleCompleto: datosBackend
            };

            this.buscado = true;
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.resultado = null;
            this.buscado = true;
            this.cdr.detectChanges();
            console.error('Consulta fallida (No encontrado o DNI incorrecto)');
          });
        }
      });
  }

  // --- MÉTODOS PARA EL POPUP DE TRAZABILIDAD ---
  abrirModalTrazabilidad() {
    if (!this.resultado || !this.resultado.id) return;

    this.reclamoService.obtenerHistorialPublico(this.resultado.id).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.historial = data;
          this.mostrarModal = true;
          this.cdr.detectChanges();
        });
      },
      error: (err) => console.error('Error al cargar la trazabilidad', err)
    });
  }

  cerrarModal() {
    this.ngZone.run(() => {
      this.mostrarModal = false;
      this.cdr.detectChanges();
    });
  }

  // --- MÉTODOS PARA EL POPUP DE DETALLES ---
  abrirModalDetalles() {
    this.ngZone.run(() => {
      this.casoSeleccionado = this.resultado.detalleCompleto;
      this.mostrarModalDetalles = true;
      this.cdr.detectChanges();
    });
  }

  cerrarModalDetalles() {
    this.ngZone.run(() => {
      this.mostrarModalDetalles = false;
      this.casoSeleccionado = null;
      this.cdr.detectChanges();
    });
  }

  // --- MÉTODOS PARA EL VISOR DE IMÁGENES ---
  abrirModalImagen(rutaArchivo: string) {
    this.ngZone.run(() => {
      this.imagenSeleccionada = this.backendUrl + encodeURI(rutaArchivo);
      this.mostrarModalImagen = true;
      this.cdr.detectChanges();
    });
  }

  cerrarModalImagen() {
    this.ngZone.run(() => {
      this.mostrarModalImagen = false;
      this.imagenSeleccionada = '';
      this.cdr.detectChanges();
    });
  }

  volver() {
    localStorage.removeItem('token');
    this.router.navigate(['/ingresar']);
  }
}
