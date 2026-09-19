import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- AGREGADO PARA LOS FILTROS
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule], // <-- AGREGADO AQUÍ TAMBIÉN
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  // Arreglos de datos
  reclamosOriginales: any[] = [];
  reclamosFiltrados: any[] = [];

  // Contadores para las tarjetas superiores
  totalCasos: number = 0;
  pendientes: number = 0;
  urgentes: number = 0;
  resueltos: number = 0;

  // Variables para el Gráfico Circular
  vencidos: number = 0;
  enProceso: number = 0;
  conicGradientString: string = 'conic-gradient(#e9ecef 0% 100%)';

  // Variables para los Filtros
  filtroPrioridad: string = 'TODAS';
  ordenFecha: string = 'DESC'; // DESC = Recientes, ASC = Antiguos

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
          this.reclamosOriginales = datosBackend.map(reclamo => ({
            id: reclamo.id,
            codigo: reclamo.codigoSeguimiento,
            dni: reclamo.usuario ? reclamo.usuario.numeroDocumento : 'Sin DNI',
            fecha: reclamo.fechaRegistro ? reclamo.fechaRegistro.split('T')[0] : 'Reciente',
            motivo: `[${reclamo.tipoSolicitud}] ${reclamo.canalCompra} - ${reclamo.productoImplicado || 'General'}`,
            prioridad: reclamo.prioridad ? reclamo.prioridad.nombre.toUpperCase() : 'MEDIA',
            estado: reclamo.estado ? reclamo.estado.nombre : 'Ingresado'
          }));

          this.calcularMetricas();
          this.aplicarFiltros(); // Aplicamos filtros y orden por defecto
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error al cargar la bandeja administrativa:', error);
      }
    });
  }

  calcularMetricas() {
    // 1. Tarjetas Superiores
    this.totalCasos = this.reclamosOriginales.length;
    this.resueltos = this.reclamosOriginales.filter(r => r.estado === 'Resuelto' || r.estado === 'Cerrado').length;
    this.urgentes = this.reclamosOriginales.filter(r => r.prioridad === 'ALTA' || r.prioridad === 'CRÍTICA').length;
    this.pendientes = this.totalCasos - this.resueltos;

    // 2. Variables del Gráfico
    this.vencidos = this.reclamosOriginales.filter(r => r.estado === 'Vencido').length;
    this.enProceso = this.reclamosOriginales.filter(r => r.estado === 'En Proceso' || r.estado === 'En Análisis' || r.estado === 'Ingresado').length;

    // 3. Generación dinámica de la dona CSS
    let totalGrafico = this.resueltos + this.vencidos + this.enProceso;
    if (totalGrafico > 0) {
      let porcResueltos = (this.resueltos / totalGrafico) * 100;
      let porcVencidos = (this.vencidos / totalGrafico) * 100;

      let stop1 = porcResueltos; // Límite Verde
      let stop2 = stop1 + porcVencidos; // Límite Rojo

      // Colores: Verde (#198754), Rojo (#dc3545), Amarillo (#ffc107)
      this.conicGradientString = `conic-gradient(#198754 0% ${stop1}%, #dc3545 ${stop1}% ${stop2}%, #ffc107 ${stop2}% 100%)`;
    }
  }

  aplicarFiltros() {
    let temp = [...this.reclamosOriginales];

    // Filtro por Prioridad
    if (this.filtroPrioridad !== 'TODAS') {
      if (this.filtroPrioridad === 'ALTA') {
        temp = temp.filter(r => r.prioridad === 'ALTA' || r.prioridad === 'CRÍTICA');
      } else {
        temp = temp.filter(r => r.prioridad === this.filtroPrioridad);
      }
    }

    // Ordenamiento por Fecha
    temp.sort((a, b) => {
      let dateA = new Date(a.fecha).getTime();
      let dateB = new Date(b.fecha).getTime();
      return this.ordenFecha === 'DESC' ? dateB - dateA : dateA - dateB;
    });

    this.reclamosFiltrados = temp;
  }

  verDetalle(id: number) {
    this.router.navigate(['/dashboard/caso', id]);
  }

  salir() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
