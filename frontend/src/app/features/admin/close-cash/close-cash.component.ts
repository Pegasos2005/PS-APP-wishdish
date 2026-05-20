import { Component, inject, signal, computed, OnInit } from '@angular/core';
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

  // Control de Vista Flujo (False = Gráfica, True = Ticket Resumen)
  showSummary = signal<boolean>(false);

  // Datos puros cargados desde el backend
  totalSales = signal<number>(0);
  totalTransactions = signal<number>(0);
  averageOrder = signal<number>(0);
  hourlyData = signal<{ hour: string, amount: number, percentage?: number }[]>([]);
  rawBackendOrders = signal<any[]>([]);

  yAxisLabels = signal<number[]>([2000, 1500, 1000, 500, 0]);

  // Signal computado para agrupar las comandas por número de mesa
  tableGroupedOrders = computed(() => {
    const orders = this.rawBackendOrders();
    if (!orders || orders.length === 0) return [];

    const groups: { [key: number]: any } = {};

    orders.forEach(order => {
      const tableNum = order.tableNumber;

      let orderTotal = 0;
      order.items.forEach((item: any) => {
        orderTotal += (item.productPrice * item.quantity);
      });

      const dateObj = order.orderDate ? new Date(order.orderDate) : new Date();
      const timeString = `${dateObj.getHours().toString().padStart(2, '0')}:${dateObj.getMinutes().toString().padStart(2, '0')}`;

      const adaptedOrder = {
        id: order.id,
        time: timeString,
        orderTotal: orderTotal,
        items: order.items
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

    // Carga de pedidos del histórico para la auditoría (o simulación en local)
    this.http.get<any[]>(`${environment.apiUrl}orders/active`).subscribe({
      next: (orders) => {
        if (!orders || orders.length === 0) {
           // Datos mockeados de prueba si las tablas están vacías
           this.rawBackendOrders.set([
             { id: 412, tableNumber: 2, orderDate: new Date(), items: [{ id: 1, quantity: 1, productName: 'Nachos', productPrice: 12.50 }, { id: 2, quantity: 1, productName: 'Solomillo', productPrice: 28.00 }] },
             { id: 415, tableNumber: 2, orderDate: new Date(), items: [{ id: 3, quantity: 2, productName: 'Nachos', productPrice: 12.50 }, { id: 4, quantity: 1, productName: 'Solomillo', productPrice: 28.00 }] },
             { id: 413, tableNumber: 5, orderDate: new Date(), items: [{ id: 5, quantity: 2, productName: 'Hamburguesa WishDish', productPrice: 18.00 }, { id: 6, quantity: 1, productName: 'Ensalada de la Casa', productPrice: 14.00 }] },
             { id: 420, tableNumber: 10, orderDate: new Date(), items: [{ id: 7, quantity: 4, productName: 'Paella Especial', productPrice: 28.00 }, { id: 8, quantity: 1, productName: 'Botella de Vino Tinto', productPrice: 25.00 }] }
           ]);
        } else {
           this.rawBackendOrders.set(orders);
        }
      }
    });
  }

  calculateChartPercentages() {
    const data = this.hourlyData();
    if (data.length === 0) return;
    const maxAmount = Math.max(...data.map(d => d.amount));
    let chartTopLimit = 10;
    if (maxAmount > 0) {
      const digits = Math.floor(Math.log10(maxAmount));
      const factor = Math.pow(10, digits - 1 >= 0 ? digits - 1 : 0);
      const step = Math.ceil(maxAmount / 4 / factor) * factor;
      chartTopLimit = step * 4;
    }
    this.yAxisLabels.set([chartTopLimit, chartTopLimit * 0.75, chartTopLimit * 0.5, chartTopLimit * 0.25, 0]);
    const updatedData = data.map(d => ({ ...d, percentage: chartTopLimit > 0 ? (d.amount / chartTopLimit) * 100 : 0 }));
    this.hourlyData.set(updatedData);
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
      // Si está en el ticket, volver atrás le regresa a la gráfica
      this.showSummary.set(false);
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  // PASO 1: Hacemos el POST al Backend, si responde OK, pasamos a la pantalla de ticket
  closeCashRegister() {
    this.http.post(`${environment.apiUrl}orders/close-cash`, {}).subscribe({
      next: () => {
        // En vez de irse al dashboard, cambiamos de vista para mostrar el resumen funcional
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

  // PASO 2: El botón final que te saca de la pantalla rumbo al dashboard
  printSummaryAndExit() {
    alert("🖨️ Summary printed successfully! Returning to dashboard.");
    this.router.navigate(['/admin/dashboard']);
  }
}
