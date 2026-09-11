import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reclamoconfirmacion',
  standalone: true,
  templateUrl: './reclamoconfirmacion.component.html'
})
export class ReclamoconfirmacionComponent {

  constructor(private router: Router) {}

  irAMisCasos() {
    this.router.navigate(['/dashboard']);
  }

  salir() {
    this.router.navigate(['/login']);
  }
}
