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

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef // 1. Inyectamos el detector de cambios
  ) {}

  consultarCaso() {
    // 2. Reiniciamos la vista para ocultar mensajes viejos mientras el servidor piensa
    this.buscado = false;
    this.resultado = null;

    this.reclamoService.consultarSeguimiento(this.consulta.codigoSeguimiento, this.consulta.numeroDocumento)
      .subscribe({
        next: (datosBackend) => {
          this.resultado = {
            codigo: datosBackend.codigoSeguimiento,
            estado: datosBackend.estado ? datosBackend.estado.nombre : 'Ingresado',
            fecha: datosBackend.fechaRegistro || 'Reciente',
            motivo: `[${datosBackend.tipoSolicitud}] - ${datosBackend.productoImplicado || 'General'}`
          };

          // 3. Activamos la vista y forzamos a Angular a pintar el éxito
          this.buscado = true;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.resultado = null;

          // 4. Activamos la vista y forzamos a Angular a pintar el error 404
          this.buscado = true;
          this.cdr.detectChanges();
          console.error('Consulta fallida (No encontrado o DNI incorrecto)');
        }
      });
  }

  volver() {
    this.router.navigate(['/login']);
  }
}
