import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

@Component({
  selector: 'app-detalle-caso',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detalle-caso.component.html',
  styleUrls: ['./detalle-caso.component.css']
})
export class DetalleCasoComponent implements OnInit {

  casoId!: number;
  reclamo: any = null;
  historial: any[] = [];

  // Modelo para el formulario de nueva nota
  nuevaNota = {
    reclamoId: 0,
    usuarioResponsableId: 2, // ID del Admin (Hardcodeado por ahora)
    estadoNuevoId: null,
    comentario: '',
    esInterno: true // Por defecto, las notas del BackOffice son privadas
  };

  // Catálogo estático de estados para el Select
  estados = [
    { id: 1, nombre: 'Ingresado' },
    { id: 2, nombre: 'En Análisis' },
    { id: 3, nombre: 'Resuelto' },
    { id: 4, nombre: 'Cerrado' },
    { id: 5, nombre: 'Vencido' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Capturamos el ID de la URL
    this.casoId = Number(this.route.snapshot.paramMap.get('id'));
    this.nuevaNota.reclamoId = this.casoId;
    this.cargarDatos();
  }

  cargarDatos() {
    // 1. Cargamos la información base del reclamo
    this.reclamoService.obtenerCasoAdmin(this.casoId).subscribe(data => {
      this.reclamo = data;
      // Preseleccionamos el estado actual en el menú desplegable
      this.nuevaNota.estadoNuevoId = data.estado.id;
      this.cdr.detectChanges();
    });

    // 2. Cargamos la línea de tiempo (Historial)
    this.reclamoService.obtenerHistorialInterno(this.casoId).subscribe(data => {
      this.historial = data;
      this.cdr.detectChanges();
    });
  }

  guardarNota() {
    if (!this.nuevaNota.comentario.trim()) return;

    this.reclamoService.registrarNotaHistorial(this.nuevaNota).subscribe({
      next: () => {
        this.nuevaNota.comentario = ''; // Limpiamos la caja de texto
        this.cargarDatos(); // Recargamos para ver la nota inmediatamente
      },
      error: (err) => console.error('Error al guardar la nota', err)
    });
  }

  volver() {
    this.router.navigate(['/dashboard']);
  }
}
