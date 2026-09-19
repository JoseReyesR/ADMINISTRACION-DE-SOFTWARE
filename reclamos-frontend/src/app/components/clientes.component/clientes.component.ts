import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.css']
})
export class ClientesComponent implements OnInit {

  clientes: any[] = [];
  cargando: boolean = true;

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDirectorioClientes();
  }

  cargarDirectorioClientes() {
    // Usamos el servicio que ya tienes configurado sin tocar el backend
    this.reclamoService.obtenerCasosAdmin().subscribe({
      next: (reclamos: any[]) => {
        // Mapa para agrupar clientes únicos por su DNI
        const clientesMap = new Map<string, any>();

        reclamos.forEach(r => {
          if (r.usuario) {
            const dni = r.usuario.numeroDocumento;

            if (clientesMap.has(dni)) {
              // Si el cliente ya existe en la lista, le sumamos 1 a sus casos totales
              clientesMap.get(dni).casosTotales += 1;
            } else {
              // Si es un cliente nuevo, lo registramos en el mapa
              clientesMap.set(dni, {
                nombre: `${r.usuario.nombres} ${r.usuario.apellidos}`,
                dni: dni,
                correo: r.usuario.correo,
                casosTotales: 1,
                // Si tu backend maneja el campo isActive lo usamos, sino asumimos ACTIVO
                estado: r.usuario.isActive !== false ? 'ACTIVO' : 'INACTIVO'
              });
            }
          }
        });

        // Convertimos el mapa en un arreglo para que el HTML lo pueda dibujar
        this.clientes = Array.from(clientesMap.values());
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
        this.cargando = false;
      }
    });
  }

  irADashboard() {
    this.router.navigate(['/dashboard']);
  }

  salir() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
