import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AccesibilidadService {

  // Estados de los botones
  altoContraste: boolean = false;
  textoGrande: boolean = false;
  resaltarEnlaces: boolean = false;
  escalaGrises: boolean = false;
  sinAnimaciones: boolean = false;

  constructor() {}

  toggleAltoContraste() {
    this.altoContraste = !this.altoContraste;
    this.actualizarClaseBody('acc-alto-contraste', this.altoContraste);
  }

  toggleTextoGrande() {
    this.textoGrande = !this.textoGrande;
    this.actualizarClaseBody('acc-texto-grande', this.textoGrande);
  }

  toggleResaltarEnlaces() {
    this.resaltarEnlaces = !this.resaltarEnlaces;
    this.actualizarClaseBody('acc-resaltar-enlaces', this.resaltarEnlaces);
  }

  toggleEscalaGrises() {
    this.escalaGrises = !this.escalaGrises;
    this.actualizarClaseBody('acc-escala-grises', this.escalaGrises);
  }

  toggleSinAnimaciones() {
    this.sinAnimaciones = !this.sinAnimaciones;
    this.actualizarClaseBody('acc-sin-animaciones', this.sinAnimaciones);
  }

  restablecerTodo() {
    this.altoContraste = false;
    this.textoGrande = false;
    this.resaltarEnlaces = false;
    this.escalaGrises = false;
    this.sinAnimaciones = false;

    document.body.classList.remove(
      'acc-alto-contraste',
      'acc-texto-grande',
      'acc-resaltar-enlaces',
      'acc-escala-grises',
      'acc-sin-animaciones'
    );
  }

  // Método central para inyectar/quitar clases al <body>
  private actualizarClaseBody(clase: string, activar: boolean) {
    if (activar) {
      document.body.classList.add(clase);
    } else {
      document.body.classList.remove(clase);
    }
  }
}
