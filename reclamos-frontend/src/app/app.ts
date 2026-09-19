import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
// Tu importación está perfecta aquí arriba
import { WidgetAccesibilidadComponent } from './components/widget-accesibilidad.component/widget-accesibilidad.component';

@Component({
  selector: 'app-root',
  // SOLUCIÓN: Agregamos WidgetAccesibilidadComponent dentro de los corchetes
  imports: [RouterOutlet, WidgetAccesibilidadComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('reclamos-frontend');
}
