import { Component, ChangeDetectorRef } from '@angular/core';
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

  // ==========================================
  // NUEVO: Variables para controlar el Popup
  // ==========================================
  mostrarModal: boolean = false;
  historial: any[] = [];
  // ==========================================

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  consultarCaso() {
    this.buscado = false;
    this.resultado = null;

    this.reclamoService.consultarSeguimiento(this.consulta.codigoSeguimiento, this.consulta.numeroDocumento)
      .subscribe({
        next: (datosBackend) => {
          this.resultado = {
            id: datosBackend.id, // NUEVO: Guardamos el ID real de la BD para buscar su historial
            codigo: datosBackend.codigoSeguimiento,
            estado: datosBackend.estado ? datosBackend.estado.nombre : 'Ingresado',
            fecha: datosBackend.fechaRegistro || 'Reciente',
            motivo: `[${datosBackend.tipoSolicitud}] - ${datosBackend.productoImplicado || 'General'}`
          };

          this.buscado = true;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.resultado = null;
          this.buscado = true;
          this.cdr.detectChanges();
          console.error('Consulta fallida (No encontrado o DNI incorrecto)');
        }
      });
  }

  // ==========================================
  // NUEVO: Métodos para la Trazabilidad
  // ==========================================
  abrirModalDetalles() {
    if (!this.resultado || !this.resultado.id) return;

    // Llamamos a la ruta pública que NO trae las notas marcadas como "esInterno"
    this.reclamoService.obtenerHistorialPublico(this.resultado.id).subscribe({
      next: (data) => {
        this.historial = data;
        this.mostrarModal = true; // Mostramos el popup
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar la trazabilidad', err)
    });
  }

  cerrarModal() {
    this.mostrarModal = false; // Ocultamos el popup
  }
  // ==========================================

  volver() {
    this.router.navigate(['/login']);
  }
}
