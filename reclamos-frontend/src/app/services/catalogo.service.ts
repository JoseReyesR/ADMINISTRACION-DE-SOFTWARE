import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  private apiUrl = 'http://localhost:8080/api/catalogos';

  constructor(private http: HttpClient) { }

  obtenerTiendas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/tiendas`);
  }

  obtenerMotivos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/motivos`);
  }
}
