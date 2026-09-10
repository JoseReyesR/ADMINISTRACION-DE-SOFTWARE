import { Component } from '@angular/core';
import { Routes } from '@angular/router';

// Importamos los componentes
import { LoginComponent } from './components/login.component/login.component';
import { DashboardComponent } from './components/dashboard.component/dashboard.component';
import { ReclamodatosComponent } from './components/reclamodatos.component/reclamodatos.component';
import { ReclamoevidenciaComponent } from './components/reclamoevidencia.component/reclamoevidencia.component';
import { ReclamoconfirmacionComponent } from './components/reclamoconfirmacion.component/reclamoconfirmacion.component';
import { ConsultainvitadoComponent } from './components/consultainvitado.component/consultainvitado.component';

export const routes: Routes = [
  // Ruta por defecto redirige al login
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Rutas públicas
  { path: 'login', component: LoginComponent },
  { path: 'consulta', component: ConsultainvitadoComponent },

  // Flujo de Registro de Reclamo
  { path: 'reclamo/datos', component: ReclamodatosComponent },
  { path: 'reclamo/evidencia', component:  ReclamoevidenciaComponent },
  { path: 'reclamo/confirmacion', component: ReclamoconfirmacionComponent },

  // Panel de Usuario (Dashboard) - Más adelante protegeremos esto con un Guard
  { path: 'dashboard', component: DashboardComponent },

  // Ruta comodín para manejar errores 404
  { path: '**', redirectTo: '/login' }
];
