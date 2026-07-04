import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { BarChartComponent, BarChartPoint } from '../../../shared/components/bar-chart/bar-chart.component';

@Component({
  selector: 'app-close-cash',
  standalone: true,
  imports: [CommonModule, BarChartComponent],
  templateUrl: './close-cash.component.html',
  styleUrls: ['./close-cash.component.css']
})
export class CloseCashComponent implements OnInit {
  private router = inject(Router);
  private http = inject(HttpClient);

  // Cabecera
  currentDateFormatted = signal<string>('');
  currentTime = signal<string>('');

  // Control de Pasos (False = Gráfica, True = Ticket Resumen)
  showSummary = signal<boolean>(false);

  // Datos reales cargados desde el backend
  totalSales = signal<number>(0);
  totalTransactions = signal<number>(0);
  averageOrder = signal<number>(0);
  hourlyData = signal<{ hour: string, amount: number }[]>([]);
  rawBackendOrders = signal<any[]>([]); // <--- Se llenará 100% con datos reales

  // Puntos para la gráfica compartida (ella calcula ejes y escalas)
  chartPoints = computed<BarChartPoint[]>(() =>
    this.hourlyData().map(d => ({ label: d.hour, amount: d.amount }))
  );

  // Agrupador dinámico por Mesas de las órdenes reales
  tableGroupedOrders = computed(() => {
    const orders = this.rawBackendOrders();
    if (!orders || orders.length === 0) return [];

    const groups: { [key: number]: any } = {};

    orders.forEach(order => {
      // Extraemos el número de mesa real de la comanda de la DB
      const tableNum = order.tableNumber || order.diningTable?.tableNumber || 0;

      // Normalizamos los items de la comanda y calculamos su subtotal real
      let orderTotal = 0;
      const normalizedItems = (order.items || []).map((item: any) => {
        const price = item.productPrice || item.unitPrice || item.price || 0;
        orderTotal += (price * item.quantity);

        return {
          id: item.id,
          quantity: item.quantity,
          productName: item.productName || item.name || item.product?.name || 'Producto',
          productPrice: price
        };
      });

      const dateObj = order.orderDate ? new Date(order.orderDate) : new Date();
      const timeString = `${dateObj.getHours().toString().padStart(2, '0')}:${dateObj.getMinutes().toString().padStart(2, '0')}`;

      const adaptedOrder = {
        id: order.id,
        time: timeString,
        orderTotal: orderTotal,
        items: normalizedItems
      };

      if (!groups[tableNum]) {
        groups[tableNum] = {
          tableNumber: tableNum,
          tableTotal: 0,
          orders: []
        };
      }

      groups[tableNum].orders.push(adaptedOrder);
      groups[tableNum].tableTotal += orderTotal;
    });

    return Object.values(groups).sort((a: any, b: any) => a.tableNumber - b.tableNumber);
  });

  ngOnInit() {
    this.generateFormattedDate();
    this.fetchDailyReport();
  }

  fetchDailyReport() {
    // ÚNICA LLAMADA AL BACKEND: Trae la gráfica y el histórico de comandas reales juntas
    this.http.get<any>(`${environment.apiUrl}orders/daily-report`).subscribe({
      next: (data) => {
        this.totalSales.set(data.totalSales || 0);
        this.totalTransactions.set(data.totalTransactions || 0);
        this.averageOrder.set(data.averageOrder || 0);
        this.hourlyData.set(data.hourlyData || []);
        this.rawBackendOrders.set(data.orders || []); // <--- COMPORTAMIENTO REAL ASIGNADO
      },
      error: (err) => console.error("Error cargando el reporte diario", err)
    });
  }

  generateFormattedDate() {
    const today = new Date();
    const dateOptions: Intl.DateTimeFormatOptions = { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' };
    let rawDate = new Intl.DateTimeFormat('es-ES', dateOptions).format(today);
    this.currentDateFormatted.set(rawDate.charAt(0).toUpperCase() + rawDate.slice(1));
    const timeOptions: Intl.DateTimeFormatOptions = { hour: '2-digit', minute: '2-digit', hour12: true };
    this.currentTime.set(new Intl.DateTimeFormat('en-US', timeOptions).format(today));
  }

  goBack() {
    if (this.showSummary()) {
      this.showSummary.set(false);
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  closeCashRegister() {
    this.http.post(`${environment.apiUrl}orders/close-cash`, {}).subscribe({
      next: () => {
        this.showSummary.set(true);
      },
      error: (err) => {
        if (err.status === 400 && err.error?.error === 'there are open tables') {
          alert("Cannot close cash register: there are open tables.");
        } else {
          alert("An unexpected error occurred while trying to close the cash register.");
        }
      }
    });
  }

  printSummaryAndExit() {
    // Las comandas pagadas se conservan en base de datos como histórico
    alert("🖨️ Summary printed successfully! Returning to dashboard.");
    this.router.navigate(['/admin/dashboard']);
  }
}
