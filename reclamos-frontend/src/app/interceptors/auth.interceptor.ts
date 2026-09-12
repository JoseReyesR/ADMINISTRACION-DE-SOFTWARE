import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Buscamos el token que guardamos al iniciar sesión
  const token = localStorage.getItem('token');

  // 2. Si el token existe, clonamos la petición y se lo pegamos en la cabecera
  if (token) {
    const peticionClonada = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(peticionClonada);
  }

  // 3. Si no hay token (ej. cuando recién nos estamos logueando o en el formulario público), pasa normal
  return next(req);
};
