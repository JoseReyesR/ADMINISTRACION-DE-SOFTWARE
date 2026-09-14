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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}


  estados: any[] = []; // Arreglo para llenar el select dinámicamente

  ngOnInit(): void {
    this.codigoCaso = this.route.snapshot.paramMap.get('codigo') || '';
    this.cargarCatalogoEstados(); // 1. Cargamos el catálogo primero

    if (this.codigoCaso) {
      this.cargarDetalleCaso();   // 2. Luego cargamos el caso
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

  cargarDetalleCaso() {
    // AHORA: Usamos la ruta exclusiva de administrador que no exige DNI
    this.reclamoService.obtenerDetalleCasoAdmin(this.codigoCaso).subscribe({
      next: (datos) => {
        this.caso = datos;
        this.nuevoEstadoId = datos.estado?.id || 1;

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
        console.error('Error al actualizar:', err);
        alert('Hubo un error al actualizar el estado.');
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
