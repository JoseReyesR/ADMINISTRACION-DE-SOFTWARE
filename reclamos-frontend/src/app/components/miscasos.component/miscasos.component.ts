import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
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
  mostrarModal: boolean = false;
  historial: any[] = [];
  mostrarModalDetalles: boolean = false;
  casoSeleccionado: any = null;

  mostrarModalImagen: boolean = false;
  imagenSeleccionada: string = '';

  // NUEVO: Ruta base de tu backend donde se sirven las imágenes
  private backendUrl = 'http://localhost:8080';

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarMisCasos();
  }

  cargarMisCasos() {
    this.reclamoService.obtenerMisCasos().subscribe({
      next: (datosBackend: any[]) => {
        this.ngZone.run(() => {
          if (datosBackend && datosBackend.length > 0) {
            this.reclamos = datosBackend.map(reclamo => ({
              id: reclamo.id,
              codigo: reclamo.codigoSeguimiento,
              fecha: reclamo.fechaCompra || reclamo.fechaRegistro || 'Sin fecha',
              motivo: `[${reclamo.tipoSolicitud}] - ${reclamo.productoImplicado || 'General'}`,
              estado: reclamo.estado ? reclamo.estado.nombre : 'Ingresado',
              detalleCompleto: reclamo
            }));
            this.cdr.detectChanges();
          } else {
            this.reclamos = [];
          }
        });
      },
      error: (error) => console.error('❌ Error al cargar la tabla:', error)
    });
  }

  abrirModalTrazabilidad(id: number) {
    if (!id) return;
    this.reclamoService.obtenerHistorialPublico(id).subscribe({
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
    this.mostrarModal = false;
  }

  abrirModalDetalles(detalle: any) {
    this.ngZone.run(() => {
      this.casoSeleccionado = detalle;
      this.mostrarModalDetalles = true;
      this.cdr.detectChanges();
    });
  }

  cerrarModalDetalles() {
    this.mostrarModalDetalles = false;
    this.casoSeleccionado = null;
  }

 // --- MÉTODO ACTUALIZADO PARA EL VISOR DE IMÁGENES ---
  abrirModalImagen(rutaArchivo: string) {
    this.ngZone.run(() => {
      // Usamos encodeURI para que los espacios se conviertan en %20 y el navegador no rompa la URL
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

  nuevoReclamo() {
    this.router.navigate(['/reclamo/datos']);
  }

  salir() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
