import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { loadStripe, Stripe, StripeElements } from '@stripe/stripe-js';
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

    let result: Awaited<ReturnType<Stripe['confirmPayment']>>;
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

    try {
      await firstValueFrom(this.paymentService.confirm(this.paymentIntentId));
      this.success.set(true);
      this.submitting.set(false);
      sessionStorage.removeItem('paymentRequested');
      this.orderService.clear();
      setTimeout(() => {
        this.router.navigate(['/customer/customer-home']);
      }, 2000);
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
}
