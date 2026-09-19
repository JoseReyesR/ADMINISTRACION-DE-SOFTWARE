import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core'; // MEJORA DE RAPIDEZ: Importamos NgZone
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

  // ==========================================
  // NUEVO: Variables para controlar el Popup
  // ==========================================
  mostrarModal: boolean = false;
  historial: any[] = [];
  // ==========================================

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone // MEJORA DE RAPIDEZ: Inyectamos NgZone
  ) {}

  ngOnInit(): void {
    this.cargarMisCasos();
  }

  cargarMisCasos() {
    this.reclamoService.obtenerMisCasos().subscribe({
      next: (datosBackend: any[]) => {
        // MEJORA DE RAPIDEZ: Forzamos la actualización inmediata
        this.ngZone.run(() => {
          console.log('📦 1. Datos del Backend:', datosBackend);

          if (datosBackend && datosBackend.length > 0) {
            this.reclamos = datosBackend.map(reclamo => ({
              id: reclamo.id, // NUEVO: Extraemos el ID numérico para buscar su historial
              codigo: reclamo.codigoSeguimiento,
              fecha: reclamo.fechaCompra || reclamo.fechaRegistro || 'Sin fecha',
              motivo: `[${reclamo.tipoSolicitud}] - ${reclamo.productoImplicado || 'General'}`,
              estado: reclamo.estado ? reclamo.estado.nombre : 'Ingresado'
            }));

            console.log('✅ 2. Datos listos para la tabla:', this.reclamos);
            this.cdr.detectChanges();
          } else {
            this.reclamos = [];
          }
        });
      },
      error: (error) => {
        console.error('❌ Error al cargar la tabla:', error);
      }
    });
  }

  // ==========================================
  // NUEVO: Métodos para la Trazabilidad
  // ==========================================
  abrirModalTrazabilidad(id: number) {
    if (!id) return;

    this.reclamoService.obtenerHistorialPublico(id).subscribe({
      next: (data) => {
        // MEJORA DE RAPIDEZ: Apertura inmediata del modal
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
    this.mostrarModal = false;
  }
  // ==========================================

  nuevoReclamo() {
    this.router.navigate(['/reclamo/datos']);
  }

  verDetalle(codigo: string) {
    console.log('Viendo detalle del caso:', codigo);
  }

  salir() {
    localStorage.removeItem('token');
    this.router.navigate(['/ingresar']);
    // NUEVO: Borramos el token del almacenamiento local para cerrar la sesión real

     // Redirigimos a la pantalla de inicio de sesión
  }
}
