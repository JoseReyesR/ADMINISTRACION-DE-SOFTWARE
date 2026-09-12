import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReclamoService {
  // Ruta de tu Backend en Spring Boot
  private apiUrl = 'http://localhost:8080/api/reclamos';

  // Objeto temporal para guardar los datos del Paso 1
  private datosCliente: any = {};

  constructor(private http: HttpClient) { }

  guardarDatosCliente(datos: any) {
    this.datosCliente = datos;
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
}
