import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReclamoService {
  // Ruta de tu Backend en Spring Boot
  private apiUrl = 'http://localhost:8080/api/reclamos';

  // NUEVO: Variable para la ruta del historial
  private historialUrl = 'http://localhost:8080/api/historial';

  // Objeto temporal para guardar los datos del Paso 1
  private datosCliente: any = {};

  constructor(private http: HttpClient) { }

  guardarDatosCliente(datos: any) {
    this.datosCliente = datos;
  }

  // --- NUEVO MÉTODO PARA LEER EL CORREO ---
  obtenerCorreoCliente(): string {
    return this.datosCliente.correo || '';
  }

  enviarReclamoTotal(datosIncidente: any, archivo: File | null): Observable<any> {
    const payload = {
      ...this.datosCliente,
      ...datosIncidente
    };
    const formData = new FormData();
    // Convertimos el JSON a Blob para que Java lo entienda junto con el archivo
    formData.append('reclamo', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

    if (archivo) {
      formData.append('archivo', archivo);
    }

    // Enviamos el JSON consolidado al Backend
    return this.http.post<any>(this.apiUrl, formData);
    }

    obtenerMisCasos(): Observable<any[]> {
    // Apunta exactamente al nuevo endpoint protegido
    return this.http.get<any[]>(`${this.apiUrl}/mis-casos`);
  }

  // NUEVO: Método para Seguimiento Invitado
  consultarSeguimiento(codigo: string, dni: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/seguimiento/${codigo}/${dni}`);
  }

  // ============================================================
  // NUEVOS MÉTODOS PARA EL BACKOFFICE ADMINISTRATIVO (HU-09)
  // ============================================================

  // 1. Obtener la bandeja completa para el Dashboard Admin
  obtenerCasosAdmin(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/todos`);
  }

  // 2. Obtener el detalle de un caso específico (sin exigir DNI)
  obtenerDetalleCasoAdmin(codigo: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin/caso/${codigo}`);
  }

  // 3. Cambiar el estado de un reclamo
  actualizarEstadoAdmin(codigo: string, idEstado: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/admin/caso/${codigo}/estado/${idEstado}`, {});
  }

  // 4. Obtener el catálogo dinámico de estados desde MySQL
  obtenerCatálogoEstados(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/estados`);
  }

  obtenerCatalogoPrioridades(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/prioridades`);
  }

  actualizarPrioridadAdmin(codigo: string, idPrioridad: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/admin/caso/${codigo}/prioridad/${idPrioridad}`, {});
  }

  // --- NUEVO MÉTODO PARA EL BACKOFFICE ---
  listarReclamosBackOffice(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/todos`);
  }


  // --- NUEVOS MÉTODOS PARA EL DETALLE Y EL HISTORIAL ---
  obtenerCasoAdmin(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin/caso/${id}`);
  }

 // --- MÉTODOS PARA EL HISTORIAL MODIFICADOS ---
  obtenerHistorialInterno(reclamoId: number): Observable<any[]> {
    // NUEVO: Usando la variable historialUrl
    return this.http.get<any[]>(`${this.historialUrl}/${reclamoId}/interno`);
  }

  registrarNotaHistorial(nota: any): Observable<any> {
    // NUEVO: Usando la variable historialUrl
    return this.http.post<any>(this.historialUrl, nota);
  }



}
