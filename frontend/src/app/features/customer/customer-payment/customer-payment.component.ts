import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { loadStripe, PaymentIntent, Stripe, StripeElements, StripeError } from '@stripe/stripe-js';
import QRCode from 'qrcode';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { PaymentService } from '../../../core/services/payment.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-customer-payment',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-payment.component.html',
  styleUrls: ['./customer-payment.component.css']
})
export class CustomerPaymentComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private orderService = inject(CustomerOrderService);
  private paymentService = inject(PaymentService);

  loading = signal<boolean>(true);
  submitting = signal<boolean>(false);
  success = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  amountEuros = signal<number>(0);

  // Datos del recibo (capturados antes de confirmar el pago)
  receiptOrders = signal<any[]>([]);
  receiptTable = signal<number | null>(null);
  receiptDate = signal<Date | null>(null);
  receiptReference = signal<string | null>(null);

  // QR con el enlace al recibo público, para llevárselo al móvil
  showQr = signal<boolean>(false);
  qrDataUrl = signal<string | null>(null);

  receiptUrl = computed(() =>
    this.receiptReference() ? `${window.location.origin}/receipt/${this.receiptReference()}` : null
  );

  // Mismos cálculos que el ticket: el precio de línea ya incluye el IGIC
  receiptTotal = computed(() => {
    if (this.receiptOrders().length === 0) return this.amountEuros();
    let total = 0;
    this.receiptOrders().forEach(order => {
      order.items.forEach((item: any) => {
        total += (item.price * item.quantity);
      });
    });
    return total;
  });

  receiptSubtotal = computed(() => this.receiptTotal() / 1.07);

  receiptTax = computed(() => this.receiptTotal() - this.receiptSubtotal());

  private stripe: Stripe | null = null;
  private elements: StripeElements | null = null;
  private paymentIntentId: string | null = null;

  async ngOnInit(): Promise<void> {
    const tableId = this.orderService.tableId();
    if (tableId === null) {
      this.router.navigate(['/join-as']);
      return;
    }

    if (!environment.stripePublicKey || environment.stripePublicKey.startsWith('pk_test_REPLACE_ME')) {
      this.errorMessage.set('Stripe no está configurado en el frontend (environment.stripePublicKey).');
      this.loading.set(false);
      return;
    }

    try {
      this.stripe = await loadStripe(environment.stripePublicKey);
      if (!this.stripe) {
        this.errorMessage.set('No se ha podido cargar Stripe.');
        this.loading.set(false);
        return;
      }

      const res = await firstValueFrom(this.paymentService.createIntent(tableId));
      this.paymentIntentId = res.paymentIntentId;
      this.amountEuros.set(res.amountCents / 100);

      this.elements = this.stripe.elements({
        clientSecret: res.clientSecret,
        appearance: { theme: 'night' }
      });

      // loading = false primero para que Angular renderice el #payment-element en el DOM,
      // luego montamos en el siguiente tick.
      this.loading.set(false);
      setTimeout(() => {
        const paymentElement = this.elements!.create('payment');
        paymentElement.mount('#payment-element');
      }, 0);
    } catch (err: any) {
      const backendMsg = err?.error?.error;
      this.errorMessage.set(backendMsg || err?.message || 'Error preparando el pago.');
      this.loading.set(false);
    }
  }

  ngOnDestroy(): void {
    this.elements = null;
    this.stripe = null;
  }

  async pay(): Promise<void> {
    if (!this.stripe || !this.elements || !this.paymentIntentId) return;

    this.submitting.set(true);
    this.errorMessage.set(null);

    let result: { paymentIntent: PaymentIntent; error?: undefined } | { paymentIntent?: undefined; error: StripeError };
    try {
      result = await this.stripe.confirmPayment({
        elements: this.elements,
        redirect: 'if_required'
      });
    } catch (stripeErr: any) {
      this.errorMessage.set(stripeErr?.message || 'Error al procesar el pago.');
      this.submitting.set(false);
      return;
    }

    if (result.error) {
      this.errorMessage.set(result.error.message || 'Pago rechazado.');
      this.submitting.set(false);
      return;
    }

    // Capturamos el ticket ANTES de confirmar: tras la confirmación las comandas
    // pasan a "paid" y el endpoint de ticket activo deja de devolverlas.
    const tableId = this.orderService.tableId();
    if (tableId !== null && this.receiptOrders().length === 0) {
      this.receiptTable.set(tableId);
      try {
        const backendOrders = await firstValueFrom(this.orderService.getTicketByTable(tableId));
        this.receiptOrders.set(this.adaptOrders(backendOrders));
      } catch {
        // Si el desglose no llega, el recibo mostrará solo el total cobrado.
      }
    }

    try {
      await firstValueFrom(this.paymentService.confirm(this.paymentIntentId));
      this.receiptReference.set(this.paymentIntentId);
      this.receiptDate.set(new Date());
      this.success.set(true);
      this.submitting.set(false);
      sessionStorage.removeItem('paymentRequested');
      this.orderService.clear();
    } catch (err: any) {
      const backendMsg = err?.error?.error;
      this.errorMessage.set(
        'El cobro se procesó en Stripe pero el servidor no pudo confirmarlo: '
        + (backendMsg || err?.message || 'inténtalo de nuevo en unos segundos.')
      );
      this.submitting.set(false);
    }
  }

  cancel(): void {
    this.router.navigate(['/customer/customer-ticket']);
  }

  printReceipt(): void {
    window.print();
  }

  async toggleQr(): Promise<void> {
    if (this.showQr()) {
      this.showQr.set(false);
      return;
    }
    const url = this.receiptUrl();
    if (url && !this.qrDataUrl()) {
      try {
        this.qrDataUrl.set(await QRCode.toDataURL(url, { width: 240, margin: 1 }));
      } catch {
        // Si el QR no se genera, se muestra igualmente el enlace en texto.
      }
    }
    this.showQr.set(true);
  }

  goHome(): void {
    this.router.navigate(['/customer/customer-home']);
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
