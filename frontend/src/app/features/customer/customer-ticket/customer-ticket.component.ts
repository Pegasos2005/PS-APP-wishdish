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
  public orderService = inject(CustomerOrderService);

  isPaymentRequested = signal<boolean>(false);
  tableOrders = signal<any[]>([]);

  restaurantName = signal<string>("WISH DISH RESTAURANT");
  currentDate = signal<Date>(new Date());

  // --- CÁLCULOS MATEMÁTICOS DEL TICKET ---

  // 1. Total Final (Con IGIC incluido)
  totalAmount = computed(() => {
    let total = 0;
    this.tableOrders().forEach(order => {
      order.items.forEach((item: any) => {
        total += (item.price * item.quantity);
      });
    });
    return total;
  });

  // 2. Subtotal (Base imponible sin el 7% de IGIC)
  subtotalAmount = computed(() => {
    // Si el TOTAL es 107%, el subtotal es TOTAL / 1.07
    return this.totalAmount() / 1.07;
  });

  // 3. El importe exacto del IGIC
  taxAmount = computed(() => {
    return this.totalAmount() - this.subtotalAmount();
  });

  ngOnInit() {
    this.loadTicketData();
  }

  loadTicketData() {
    const currentTable = this.orderService.tableId();

    if (currentTable) {
      this.orderService.getTicketByTable(currentTable).subscribe({
        next: (backendOrders) => {
          const adaptedOrders = backendOrders.map((order, index) => {
            const dateObj = order.orderDate ? new Date(order.orderDate) : new Date();
            const timeString = `${dateObj.getHours().toString().padStart(2, '0')}:${dateObj.getMinutes().toString().padStart(2, '0')}`;

            return {
              commandNumber: index + 1,
              time: timeString,
              items: order.items.map((item: any) => {

                // ¡CORRECCIÓN AQUÍ! Leemos 'extras' (como lo envía el nuevo DTO de Java)
                const extrasDelBackend = item.extras || [];
                const quitadosDelBackend = item.removedDefaults || [];

                const mappedExtras = extrasDelBackend.map((extra: any) => {
                  return { name: extra.name, price: extra.price };
                });

                return {
                  quantity: item.quantity,
                  name: item.productName || item.product?.name,
                  price: item.productPrice || item.unitPrice || item.product?.price,
                  extras: mappedExtras,
                  removed: quitadosDelBackend
                };
              })
            };
          });

          this.tableOrders.set(adaptedOrders);
        },
        error: (err) => console.error("Error loading ticket:", err)
      });
    }
  }

  requestPayment(): void {
    this.isPaymentRequested.set(true);
  }

  cancelPayment(): void {
    this.isPaymentRequested.set(false);
  }
}
