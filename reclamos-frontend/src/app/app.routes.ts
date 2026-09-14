import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

// Importamos los componentes
import { LoginComponent } from './components/login.component/login.component';
import { DashboardComponent } from './components/dashboard.component/dashboard.component';
import { ReclamodatosComponent } from './components/reclamodatos.component/reclamodatos.component';
import { ReclamoevidenciaComponent } from './components/reclamoevidencia.component/reclamoevidencia.component';
import { ReclamoconfirmacionComponent } from './components/reclamoconfirmacion.component/reclamoconfirmacion.component';
import { ConsultainvitadoComponent } from './components/consultainvitado.component/consultainvitado.component';
import { IngresoClienteComponent } from './components/ingreso-cliente.component/ingreso-cliente.component';
import { MiscasosComponent } from './components/miscasos.component/miscasos.component';
import { DetallecasoComponent } from './components/detallecaso.component/detallecaso.component';

export const routes: Routes = [
  // Ruta por defecto redirige al login
{ path: '', redirectTo: 'ingresar', pathMatch: 'full' }, // El cliente entra directo a su formulario

  // Rutas públicas

  { path: 'consulta', component: ConsultainvitadoComponent },

  // Flujo de Registro de Reclamo
  { path: 'reclamo/datos', component: ReclamodatosComponent },
  { path: 'reclamo/evidencia', component:  ReclamoevidenciaComponent },
  { path: 'reclamo/confirmacion', component: ReclamoconfirmacionComponent },

 // RUTA PÚBLICA (Pero exclusiva para clientes)
  { path: 'ingresar', component: IngresoClienteComponent },
  { path: 'mis-casos', component: MiscasosComponent },

  // RUTA PÚBLICA (Pero exclusiva para trabajadores)
  { path: 'login', component: LoginComponent },

  // RUTAS PRIVADAS (PROTEGIDAS POR EL GUARD)
  // RUTAS PRIVADAS (BackOffice)
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'dashboard/caso/:codigo', component: DetallecasoComponent },
  // ... aquí irán otras rutas de administración (ej. listado de reclamos)
  // Ruta comodín para manejar errores 404
  { path: '**', redirectTo: '/login' }
];
