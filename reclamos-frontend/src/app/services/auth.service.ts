import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth/login';

  constructor(private http: HttpClient) { }

  login(credenciales: { correo: string; password: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, credenciales).pipe(
      tap(response => {
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

  obtenerRol(): string | null {
    const token = this.obtenerToken();
    if (token) {
      try {
        const decodificado: any = jwtDecode(token);
        return decodificado.rol;
      } catch (e) {
        return null;
      }
    }
    return null;
  }
}
