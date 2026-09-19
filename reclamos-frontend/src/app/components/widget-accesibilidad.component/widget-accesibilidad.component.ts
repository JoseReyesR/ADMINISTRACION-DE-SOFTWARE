import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccesibilidadService } from '../../services/accesibilidad.service';

@Component({
  selector: 'app-widget-accesibilidad',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './widget-accesibilidad.component.html',
  styleUrls: ['./widget-accesibilidad.component.css']
})
export class WidgetAccesibilidadComponent {

  menuAbierto: boolean = false;

  constructor(public accService: AccesibilidadService) {}

  toggleMenu() {
    this.menuAbierto = !this.menuAbierto;
  }
}
