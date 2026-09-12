import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode'; // Importación de la librería

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Ruta de tu Backend para el login
  private apiUrl = 'http://localhost:8080/api/auth/login';

  constructor(private http: HttpClient) { }

  login(credenciales: { correo: string; password: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, credenciales).pipe(
      tap(response => {
        // Si el backend responde con un token válido, lo guardamos
        if (response && response.token) {
          localStorage.setItem('token', response.token);
        }
      })
    );
  }

  obtenerToken(): string | null {
    return localStorage.getItem('token');
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
  }

  // Método que decodifica el token para saber quién inició sesión
  obtenerRol(): string | null {
    const token = this.obtenerToken();
    if (token) {
      try {
        const decodificado: any = jwtDecode(token);
        return decodificado.rol; // Extrae la variable "rol" que inyectamos en Java
      } catch (Error) {
        return null;
      }
    }
    return null;
  }
}
