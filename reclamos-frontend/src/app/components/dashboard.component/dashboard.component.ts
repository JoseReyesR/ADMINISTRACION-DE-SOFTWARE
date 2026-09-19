import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- AGREGADO PARA LOS FILTROS
import { Router } from '@angular/router';
import { ReclamoService } from '../../services/reclamo.service';


// ---> NUEVO: Importaciones para los reportes
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule], // <-- AGREGADO AQUÍ TAMBIÉN
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  // Arreglos de datos
  reclamosOriginales: any[] = [];
  reclamosFiltrados: any[] = [];

  // Contadores para las tarjetas superiores
  totalCasos: number = 0;
  pendientes: number = 0;
  urgentes: number = 0;
  resueltos: number = 0;

  // Variables para el Gráfico Circular
  vencidos: number = 0;
  enProceso: number = 0;
  conicGradientString: string = 'conic-gradient(#e9ecef 0% 100%)';

  // Variables para los Filtros
  filtroPrioridad: string = 'TODAS';
  ordenFecha: string = 'DESC'; // DESC = Recientes, ASC = Antiguos

  constructor(
    private router: Router,
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarBandejaAdmin();
  }

  cargarBandejaAdmin() {
    this.reclamoService.obtenerCasosAdmin().subscribe({
      next: (datosBackend: any[]) => {
        if (datosBackend && datosBackend.length > 0) {
          this.reclamosOriginales = datosBackend.map(reclamo => ({
            id: reclamo.id,
            codigo: reclamo.codigoSeguimiento,
            dni: reclamo.usuario ? reclamo.usuario.numeroDocumento : 'Sin DNI',
            fecha: reclamo.fechaRegistro ? reclamo.fechaRegistro.split('T')[0] : 'Reciente',
            motivo: `[${reclamo.tipoSolicitud}] ${reclamo.canalCompra} - ${reclamo.productoImplicado || 'General'}`,
            prioridad: reclamo.prioridad ? reclamo.prioridad.nombre.toUpperCase() : 'MEDIA',
            estado: reclamo.estado ? reclamo.estado.nombre : 'Ingresado'
          }));

          this.calcularMetricas();
          this.aplicarFiltros(); // Aplicamos filtros y orden por defecto
          this.cdr.detectChanges();
        }
      },
      error: (error) => {
        console.error('Error al cargar la bandeja administrativa:', error);
      }
    });
  }

  calcularMetricas() {
    // 1. Tarjetas Superiores
    this.totalCasos = this.reclamosOriginales.length;
    this.resueltos = this.reclamosOriginales.filter(r => r.estado === 'Resuelto' || r.estado === 'Cerrado').length;
    this.urgentes = this.reclamosOriginales.filter(r => r.prioridad === 'ALTA' || r.prioridad === 'CRÍTICA').length;
    this.pendientes = this.totalCasos - this.resueltos;

    // 2. Variables del Gráfico
    this.vencidos = this.reclamosOriginales.filter(r => r.estado === 'Vencido').length;
    this.enProceso = this.reclamosOriginales.filter(r => r.estado === 'En Proceso' || r.estado === 'En Análisis' || r.estado === 'Ingresado').length;

    // 3. Generación dinámica de la dona CSS
    let totalGrafico = this.resueltos + this.vencidos + this.enProceso;
    if (totalGrafico > 0) {
      let porcResueltos = (this.resueltos / totalGrafico) * 100;
      let porcVencidos = (this.vencidos / totalGrafico) * 100;

      let stop1 = porcResueltos; // Límite Verde
      let stop2 = stop1 + porcVencidos; // Límite Rojo

      // Colores: Verde (#198754), Rojo (#dc3545), Amarillo (#ffc107)
      this.conicGradientString = `conic-gradient(#198754 0% ${stop1}%, #dc3545 ${stop1}% ${stop2}%, #ffc107 ${stop2}% 100%)`;
    }
  }

  aplicarFiltros() {
    let temp = [...this.reclamosOriginales];

    // Filtro por Prioridad
    if (this.filtroPrioridad !== 'TODAS') {
      if (this.filtroPrioridad === 'ALTA') {
        temp = temp.filter(r => r.prioridad === 'ALTA' || r.prioridad === 'CRÍTICA');
      } else {
        temp = temp.filter(r => r.prioridad === this.filtroPrioridad);
      }
    }

    // Ordenamiento por Fecha
    temp.sort((a, b) => {
      let dateA = new Date(a.fecha).getTime();
      let dateB = new Date(b.fecha).getTime();
      return this.ordenFecha === 'DESC' ? dateB - dateA : dateA - dateB;
    });

    this.reclamosFiltrados = temp;
  }

  verDetalle(id: number) {
    this.router.navigate(['/dashboard/caso', id]);
  }

  salir() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
 irAClientes() {
    this.router.navigate(['/clientes']);
  }


  // =========================================================
  // NUEVO: MÉTODOS DE EXPORTACIÓN DE REPORTES (RF08)
  // =========================================================

  exportarExcel() {
    // 1. Preparamos los datos limpios para el Excel
    const datosExcel = this.reclamosFiltrados.map(r => ({
      'Código de Seguimiento': r.codigo,
      'Documento (DNI/RUC)': r.dni,
      'Fecha de Registro': r.fecha,
      'Motivo y Canal': r.motivo,
      'Nivel de Prioridad': r.prioridad,
      'Estado Actual': r.estado
    }));

    // 2. Creamos el libro y la hoja de cálculo
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(datosExcel);
    const workbook: XLSX.WorkBook = { Sheets: { 'Reclamos': worksheet }, SheetNames: ['Reclamos'] };

    // 3. Generamos el archivo físico y forzamos la descarga
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const data: Blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(data, `Reporte_Reclamos_${new Date().getTime()}.xlsx`);
  }

  exportarPDF() {
    // 1. Iniciamos el documento PDF en formato horizontal (landscape)
    const doc = new jsPDF('landscape');

    // 2. Título del Reporte
    doc.setFontSize(18);
    doc.text('Reporte Gerencial de Reclamos', 14, 22);
    doc.setFontSize(11);
    doc.setTextColor(100);
    doc.text(`Generado el: ${new Date().toLocaleDateString()}`, 14, 30);

    // 3. Preparamos las columnas y filas para la tabla
    const columnas = [['CÓDIGO', 'DNI', 'FECHA', 'MOTIVO', 'PRIORIDAD', 'ESTADO']];
    const filas = this.reclamosFiltrados.map(r => [
      r.codigo,
      r.dni,
      r.fecha,
      r.motivo,
      r.prioridad,
      r.estado
    ]);

    // 4. Dibujamos la tabla automáticamente
    autoTable(doc, {
      head: columnas,
      body: filas,
      startY: 40,
      theme: 'grid',
      headStyles: { fillColor: [123, 179, 46] } // Color Verde Tottus
    });

    // 5. Descargamos el archivo
    doc.save(`Reporte_Reclamos_${new Date().getTime()}.pdf`);
  }
}
