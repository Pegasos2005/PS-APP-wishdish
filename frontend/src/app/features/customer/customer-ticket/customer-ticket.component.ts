// src/app/features/customer/customer-ticket/customer-ticket.component.ts
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CustomerOrderService } from '../../../core/services/customer-order.service';

@Component({
  selector: 'app-customer-ticket',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-ticket.component.html',
  styleUrls: ['./customer-ticket.component.css']
})
export class CustomerTicketComponent implements OnInit {
  private router = inject(Router);
  private orderService = inject(CustomerOrderService);

  isPaymentRequested = signal<boolean>(false);
  tableId = 1; // En un futuro, esto vendrá del inicio de sesión/QR del cliente

  // Nuestro Signal ahora empieza vacío
  tableOrders = signal<any[]>([]);

  // El computed se actualiza solo en cuanto el backend nos devuelve los datos
  totalAmount = computed(() => {
    let total = 0;
    this.tableOrders().forEach(order => {
      order.items.forEach((item: any) => {
        total += (item.price * item.quantity);
      });
    });
    return total;
  });

  ngOnInit() {
    this.loadTicketData();
  }

  loadTicketData() {
      this.orderService.getTicketByTable(this.tableId).subscribe({
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

    // El total amount sumará correctamente los precios recibidos
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
  }

  requestPayment(): void {
    this.isPaymentRequested.set(true);
    // Próximo paso: this.ticketService.notifyWaiter(this.tableId).subscribe()
  }

  cancelPayment(): void {
    this.isPaymentRequested.set(false);
  }
}
