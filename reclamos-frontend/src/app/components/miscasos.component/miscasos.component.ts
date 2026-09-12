import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

@Component({
  selector: 'app-miscasos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './miscasos.component.html',
  styleUrls: ['./miscasos.component.css']
})
export class MiscasosComponent implements OnInit {

  reclamos: any[] = [];

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef // Inyectamos el detector de cambios de Angular
  ) {}

  ngOnInit(): void {
    this.cargarMisCasos();
  }

  cargarMisCasos() {
    this.reclamoService.obtenerMisCasos().subscribe({
      next: (datosBackend: any[]) => {
        // 1. Verificamos que lleguen los datos
        console.log('📦 1. Datos del Backend:', datosBackend);

        if (datosBackend && datosBackend.length > 0) {
          // 2. Mapeamos la data
          this.reclamos = datosBackend.map(reclamo => ({
            codigo: reclamo.codigoSeguimiento,
            fecha: reclamo.fechaCompra || reclamo.fechaRegistro || 'Sin fecha',
            motivo: `[${reclamo.tipoSolicitud}] - ${reclamo.productoImplicado || 'General'}`,
            estado: reclamo.estado ? reclamo.estado.nombre : 'Ingresado'
          }));

          // 3. Imprimimos para confirmar que el mapeo funcionó
          console.log('✅ 2. Datos listos para la tabla:', this.reclamos);

          // 4. ¡LA CLAVE! Obligamos a Angular a actualizar el HTML en este instante
          this.cdr.detectChanges();
        } else {
          this.reclamos = [];
        }
      },
      error: (error) => {
        console.error('❌ Error al cargar la tabla:', error);
      }
    });
  }

  nuevoReclamo() {
    this.router.navigate(['/reclamo/datos']);
  }

  verDetalle(codigo: string) {
    console.log('Viendo detalle del caso:', codigo);
  }

  salir() {
    this.router.navigate(['/ingresar']);
  }
}
