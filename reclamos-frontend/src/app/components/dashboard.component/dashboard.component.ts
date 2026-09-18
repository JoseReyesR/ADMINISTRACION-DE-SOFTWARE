import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  reclamos: any[] = [];

  // Contadores para las tarjetas superiores (Resumen de Operaciones)
  totalCasos: number = 0;
  pendientes: number = 0;
  urgentes: number = 0;
  resueltos: number = 0;

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarBandejaAdmin();
  }

  cargarBandejaAdmin() {
    this.reclamoService.obtenerCasosAdmin().subscribe({
      next: (datosBackend: any[]) => {
        if (datosBackend && datosBackend.length > 0) {
          // Mapeamos los datos para la tabla del BackOffice
          this.reclamos = datosBackend.map(reclamo => ({
            id: reclamo.id, // <-- AGREGADO: Necesario para consultar el historial en la base de datos
            codigo: reclamo.codigoSeguimiento,
            dni: reclamo.usuario ? reclamo.usuario.numeroDocumento : 'Sin DNI',
            fecha: reclamo.fechaRegistro ? reclamo.fechaRegistro.split('T')[0] : 'Reciente',
            motivo: `[${reclamo.tipoSolicitud}] ${reclamo.canalCompra} - ${reclamo.productoImplicado || 'General'}`,
            prioridad: reclamo.prioridad ? reclamo.prioridad.nombre.toUpperCase() : 'MEDIA',
            estado: reclamo.estado ? reclamo.estado.nombre : 'Ingresado'
          }));

          this.calcularMetricas();
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error al cargar la bandeja administrativa:', error);
      }
    });
  }

  calcularMetricas() {
    this.totalCasos = this.reclamos.length;
    this.pendientes = this.reclamos.filter(r => r.estado !== 'Resuelto' && r.estado !== 'Cerrado').length;
    this.urgentes = this.reclamos.filter(r => r.prioridad === 'ALTA').length;
    this.resueltos = this.reclamos.filter(r => r.estado === 'Resuelto').length;
  }

  // MODIFICADO: Ahora recibe el ID (number) en lugar del código (string)
  verDetalle(id: number) {
    // Redirige a la vista de detalle del caso seleccionado
    this.router.navigate(['/dashboard/caso', id]);
  }

  salir() {
    localStorage.removeItem('token'); // Limpiamos la sesión
    this.router.navigate(['/login']);
  }
}
