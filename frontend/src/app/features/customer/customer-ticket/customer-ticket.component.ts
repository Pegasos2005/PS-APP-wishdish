// src/app/features/customer/customer-ticket/customer-ticket.component.ts
import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription, interval, switchMap } from 'rxjs';
import { CustomerOrderService } from '../../../core/services/customer-order.service';

@Component({
  selector: 'app-customer-ticket',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-ticket.component.html',
  styleUrls: ['./customer-ticket.component.css']
})
export class CustomerTicketComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  protected orderService = inject(CustomerOrderService);

  isPaymentRequested = signal<boolean>(false);

  // Nuestro Signal ahora empieza vacío

  tableOrders = signal<any[]>([]);

  private statusPollSub?: Subscription;

  // El total amount sumará correctamente los precios recibidos (Solo una declaración aquí)
  totalAmount = computed(() => {
    let total = 0;
    this.tableOrders().forEach(order => {
      order.items.forEach((item: any) => {
        // Multiplicamos cantidad por el precio (que viene de BigDecimal)
        total += (item.price * item.quantity);
      });
    });
    return total;
  });

  ngOnInit() {
    this.loadTicketData();
    if (sessionStorage.getItem('paymentRequested') === 'true') {
      this.isPaymentRequested.set(true);
      this.startStatusPolling();
    }
  }

  ngOnDestroy() {
    this.statusPollSub?.unsubscribe();
  }

  loadTicketData() {
    const tableId = this.orderService.tableId();
    if (tableId === null) {
      this.router.navigate(['/join-as']);
      return;
    }
    this.orderService.getTicketByTable(tableId).subscribe({
      next: (backendOrders) => {
        const adaptedOrders = backendOrders.map((order, index) => {
          const dateObj = order.orderDate ? new Date(order.orderDate) : new Date();
          const timeString = `${dateObj.getHours().toString().padStart(2, '0')}:${dateObj.getMinutes().toString().padStart(2, '0')}`;

          return {
            commandNumber: index + 1,
            time: timeString,
            items: order.items.map((item: any) => ({
              quantity: item.quantity,
              // Leemos las variables exactas del DTO: productName y productPrice
              name: item.productName,
              price: item.productPrice
            }))
          };
        });

        this.tableOrders.set(adaptedOrders);
      },
      error: (err) => console.error("Error loading ticket:", err)
    });
  }

  requestPayment(): void {
    const tableId = this.orderService.tableId();
    if (tableId === null) {
      this.router.navigate(['/join-as']);
      return;
    }
    this.orderService.requestPayment(tableId).subscribe({
      next: () => {
        this.isPaymentRequested.set(true);
        sessionStorage.setItem('paymentRequested', 'true');
        this.startStatusPolling();
      },
      error: (err) => console.error("Error requesting payment:", err)
    });
  }

  cancelPayment(): void {
    this.isPaymentRequested.set(false);
  }

  private startStatusPolling(): void {
    const tableId = this.orderService.tableId();
    if (tableId === null) return;
    this.statusPollSub?.unsubscribe();
    this.statusPollSub = interval(3000)
      .pipe(switchMap(() => this.orderService.getTableStatus(tableId)))
      .subscribe({
        next: (status) => {
          if (!status.paymentRequested && !status.hasActiveOrders) {
            this.onTableClosed();
          }
        },
        error: (err) => console.error("Error polling table status:", err)
      });
  }

  private onTableClosed(): void {
    this.statusPollSub?.unsubscribe();
    sessionStorage.removeItem('paymentRequested');
    this.orderService.clear();
    this.isPaymentRequested.set(false);
    this.tableOrders.set([]);
    this.router.navigate(['/customer/customer-home']);
  }
}
