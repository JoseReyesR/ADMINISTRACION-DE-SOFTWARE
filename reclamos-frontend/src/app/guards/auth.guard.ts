import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  // Inyectamos tu AuthService y el Router para poder redireccionar
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    // Verificamos si existe el token
    if (this.authService.obtenerToken()) {
      return true; // ¡Tiene token, lo dejamos pasar a la pantalla!
    } else {
      this.router.navigate(['/login']); // No tiene token, lo pateamos al login
      return false;
    }
  }
}
