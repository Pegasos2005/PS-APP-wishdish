import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { PaymentService } from '../../../core/services/payment.service';

@Component({
  selector: 'app-receipt-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './receipt-view.component.html',
  styleUrls: ['./receipt-view.component.css']
})
export class ReceiptViewComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private paymentService = inject(PaymentService);

  loading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  receipt = signal<any | null>(null);
  orders = signal<any[]>([]);

  // Mismos cálculos que el ticket: el precio de línea ya incluye el IGIC
  total = computed(() => {
    if (this.orders().length === 0) return this.receipt()?.amount ?? 0;
    let total = 0;
    this.orders().forEach(order => {
      order.items.forEach((item: any) => {
        total += (item.price * item.quantity);
      });
    });
    return total;
  });

  subtotal = computed(() => this.total() / 1.07);

  tax = computed(() => this.total() - this.subtotal());

  async ngOnInit(): Promise<void> {
    const reference = this.route.snapshot.paramMap.get('ref');
    if (!reference) {
      this.errorMessage.set('No se ha encontrado el recibo.');
      this.loading.set(false);
      return;
    }

    try {
      const receipt = await firstValueFrom(this.paymentService.getReceipt(reference));
      this.receipt.set(receipt);
      this.orders.set(this.adaptOrders(receipt.orders || []));
    } catch (err: any) {
      this.errorMessage.set(err?.error?.error || 'No se ha encontrado el recibo.');
    } finally {
      this.loading.set(false);
    }
  }

  download(): void {
    window.print();
  }

  private adaptOrders(backendOrders: any[]): any[] {
    return backendOrders.map((order, index) => {
      const dateObj = order.orderDate ? new Date(order.orderDate) : new Date();
      const timeString = `${dateObj.getHours().toString().padStart(2, '0')}:${dateObj.getMinutes().toString().padStart(2, '0')}`;

      return {
        commandNumber: index + 1,
        time: timeString,
        items: order.items.map((item: any) => ({
          quantity: item.quantity,
          name: item.productName || item.product?.name,
          price: item.productPrice || item.unitPrice || item.product?.price,
          extras: (item.extras || []).map((extra: any) => ({ name: extra.name, price: extra.price })),
          removed: item.removedDefaults || []
        }))
      };
    });
  }
}
