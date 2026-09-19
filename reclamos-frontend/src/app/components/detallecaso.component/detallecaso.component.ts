import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core'; // modificado: Inyectamos NgZone
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-detallecaso',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detallecaso.component.html',
  styleUrls: ['./detallecaso.component.css']
})
export class DetallecasoComponent implements OnInit {

  codigoCaso: string = '';
  caso: any = null;

  nuevoEstadoId: number = 1; // Por defecto
  estados: any[] = []; // Arreglo para llenar el select dinámicamente

  nuevaPrioridadId: number = 2; // Media por defecto
  prioridades: any[] = []; // Arreglo para prioridades
   // NUEVO: Variables para el historial y notas

  historial: any[] = [];
  nuevaNota = {
    reclamoId: 0,
    usuarioResponsableId: 2,// ID de un Administrador en BD
    estadoNuevoId: null,// Dejamos null para no cruzarlo con tu cambio de estado principal
    comentario: '',
    esInterno: true
  };

  // =========================================================
  // NUEVO: Variables para el Visor de Imágenes
  // =========================================================
  mostrarModalImagen: boolean = false;
  imagenSeleccionada: string = '';
  private backendUrl = 'http://localhost:8080';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone // modificado: Inyectamos el NgZone
  ) {}

  ngOnInit(): void {
    this.codigoCaso = this.route.snapshot.paramMap.get('codigo') || '';
    // 1. Cargamos TODOS los catálogos primero
    this.cargarCatalogoEstados();
    this.cargarCatalogoPrioridades();
// 2. Luego cargamos el caso
    if (this.codigoCaso) {
      this.cargarDetalleCaso();
    }
  }

  cargarCatalogoEstados() {
    this.reclamoService.obtenerCatálogoEstados().subscribe({
      next: (data) => {
        this.estados = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error cargando los estados:', err)
    });
  }

  cargarCatalogoPrioridades() {
    this.reclamoService.obtenerCatalogoPrioridades().subscribe({
      next: (data) => {
        this.prioridades = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error cargando prioridades:', err)
    });
  }

  cargarDetalleCaso() {
    // Usamos la ruta exclusiva de administrador que no exige DNI
    this.reclamoService.obtenerDetalleCasoAdmin(this.codigoCaso).subscribe({
      next: (datos) => {
        this.ngZone.run(() => {
          this.caso = datos;
          this.nuevoEstadoId = datos.estado?.id || 1;
          this.nuevaPrioridadId = datos.prioridad?.id || 2;
 // NUEVO: Asignamos el ID numérico real del caso y cargamos su historial
          if (datos.id) {
            this.nuevaNota.reclamoId = datos.id;
            this.cargarHistorial();
          }
 // Obligamos a Angular a quitar el mensaje de "Cargando" y pintar los datos
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        console.error('Error al cargar el caso administrativo:', err);
        alert('No se pudo cargar el caso. Revisa la consola.');
      }
    });
  }
// NUEVO: Métodos para cargar y guardar notas en el historial
  cargarHistorial() {
    if (!this.caso || !this.caso.id) return;
    this.reclamoService.obtenerHistorialInterno(this.caso.id).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.historial = data;
          this.cdr.detectChanges();
        });
      },
      error: (err) => console.error('Error cargando historial:', err)
    });
  }

  guardarNota() {
    if (!this.nuevaNota.comentario.trim()) {
      alert('Debe escribir un comentario antes de guardar.');
      return;
    }

    this.reclamoService.registrarNotaHistorial(this.nuevaNota).subscribe({
      next: () => {
        alert('Nota guardada correctamente.');
        this.nuevaNota.comentario = ''; // Limpiar textarea
        this.cargarHistorial(); // Recargamos solo la línea de tiempo
      },
      error: (err) => {
        console.error('Error al guardar la nota:', err);
        alert('Hubo un error al intentar guardar la nota.');
      }
    });
  }

  guardarCambioEstado() {
    this.reclamoService.actualizarEstadoAdmin(this.codigoCaso, Number(this.nuevoEstadoId)).subscribe({
      next: (res) => {
        alert('¡Estado actualizado correctamente!');
        this.cargarDetalleCaso(); // Recargamos para ver los cambios
      },
      error: (err) => {
        console.error('Error al actualizar estado:', err);
        alert('Hubo un error al actualizar el estado.');
      }
    });
  }

  guardarCambioPrioridad() {
    this.reclamoService.actualizarPrioridadAdmin(this.codigoCaso, Number(this.nuevaPrioridadId)).subscribe({
      next: (res) => {
        alert('¡Prioridad actualizada correctamente!');
        this.cargarDetalleCaso();  // Recargamos para ver los cambios
      },
      error: (err) => {
        console.error('Error al actualizar prioridad:', err);
        alert('Hubo un error al actualizar la prioridad.');
      }
    });
  }

  // =========================================================
  // NUEVO: Métodos para el visor de imágenes
  // =========================================================
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

  volverBandeja() {
    this.router.navigate(['/dashboard']);
  }

  salir() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
