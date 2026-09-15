import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.codigoCaso = this.route.snapshot.paramMap.get('codigo') || '';

    // 1. Cargamos TODOS los catálogos primero
    this.cargarCatalogoEstados();
    this.cargarCatalogoPrioridades(); // <--- ¡ESTA ERA LA LÍNEA FALTANTE!

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
        this.caso = datos;
        this.nuevoEstadoId = datos.estado?.id || 1;
        this.nuevaPrioridadId = datos.prioridad?.id || 2;

        // Obligamos a Angular a quitar el mensaje de "Cargando" y pintar los datos
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar el caso administrativo:', err);
        alert('No se pudo cargar el caso. Revisa la consola.');
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
        this.cargarDetalleCaso(); // Recargamos para ver los cambios
      },
      error: (err) => {
        console.error('Error al actualizar prioridad:', err);
        alert('Hubo un error al actualizar la prioridad.');
      }
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
