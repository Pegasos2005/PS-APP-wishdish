import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-close-cash',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './close-cash.component.html',
  styleUrls: ['./close-cash.component.css']
})
export class CloseCashComponent implements OnInit {
  private router = inject(Router);
  private http = inject(HttpClient);

  // Cabecera
  currentDateFormatted = signal<string>('');
  currentTime = signal<string>('');

  // Datos reales del backend (Inician a cero)
  totalSales = signal<number>(0);
  totalTransactions = signal<number>(0);
  averageOrder = signal<number>(0);

  hourlyData = signal<{ hour: string, amount: number, percentage?: number }[]>([]);
  yAxisLabels = signal<number[]>([2000, 1500, 1000, 500, 0]);

  ngOnInit() {
    this.generateFormattedDate();
    this.fetchDailyReport();
  }

  // Llamada a tu nuevo endpoint de Spring Boot
  fetchDailyReport() {
    this.http.get<any>(`${environment.apiUrl}orders/daily-report`).subscribe({
      next: (data) => {
        this.totalSales.set(data.totalSales);
        this.totalTransactions.set(data.totalTransactions);
        this.averageOrder.set(data.averageOrder);
        this.hourlyData.set(data.hourlyData);

        this.calculateChartPercentages();
      },
      error: (err) => console.error("Error cargando el reporte diario", err)
    });
  }

  // Calcula la altura de cada barra y los textos laterales (eje Y)
  // Calcula la altura de cada barra y los textos laterales (eje Y)
    calculateChartPercentages() {
      const data = this.hourlyData();
      if (data.length === 0) return;

      // Buscamos cuál es la hora en la que más se vendió
      const maxAmount = Math.max(...data.map(d => d.amount));

      // Matemática dinámica para adaptar la escala al máximo real
      let chartTopLimit = 10; // Mínimo absoluto si no hay apenas ventas
      if (maxAmount > 0) {
        // Calculamos el orden de magnitud (unidades, decenas, cientos...)
        const digits = Math.floor(Math.log10(maxAmount));
        const factor = Math.pow(10, digits - 1 >= 0 ? digits - 1 : 0);

        // Creamos escalones exactos dividiendo entre las 4 franjas que tenemos
        const step = Math.ceil(maxAmount / 4 / factor) * factor;
        chartTopLimit = step * 4;
      }

      // Generar etiquetas dinámicas para el Eje Y
      this.yAxisLabels.set([
        chartTopLimit,
        chartTopLimit * 0.75,
        chartTopLimit * 0.5,
        chartTopLimit * 0.25,
        0
      ]);

      // Calcular qué % de altura ocupa cada barrita según el nuevo techo
      const updatedData = data.map(d => ({
        ...d,
        percentage: chartTopLimit > 0 ? (d.amount / chartTopLimit) * 100 : 0
      }));

      this.hourlyData.set(updatedData);
    }

  // Genera la fecha actual con el formato correcto
  generateFormattedDate() {
    const today = new Date();

    // Fecha (Miércoles, 20/05/2026)
    const dateOptions: Intl.DateTimeFormatOptions = { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' };
    let rawDate = new Intl.DateTimeFormat('es-ES', dateOptions).format(today);
    this.currentDateFormatted.set(rawDate.charAt(0).toUpperCase() + rawDate.slice(1));

    // Hora (23:45 PM)
    const timeOptions: Intl.DateTimeFormatOptions = { hour: '2-digit', minute: '2-digit', hour12: true };
    this.currentTime.set(new Intl.DateTimeFormat('en-US', timeOptions).format(today));
  }

  // Navegación hacia atrás
  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }

  // Lógica del botón de Cierre de Caja
  closeCashRegister() {
    alert("Cerrando caja... (Próximamente conexión al Backend)");
  }
}
