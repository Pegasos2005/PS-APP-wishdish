import { Component, OnInit, signal, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { interval, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { PaymentService } from '../../../core/services/payment.service';

@Component({
  selector: 'app-close-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './close-table.component.html',
  styleUrls: ['./close-table.component.css']
})
export class CloseTableComponent implements OnInit {
  private orderService = inject(CustomerOrderService);
  private paymentService = inject(PaymentService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  tablesAwaitingPayment = signal<number[]>([]);

  // Cobros completados en los últimos minutos (aviso para el personal)
  recentPayments = signal<any[]>([]);

  ngOnInit() {
    this.startPaymentRequestsPolling();
    this.startRecentPaymentsPolling();
  }

  startPaymentRequestsPolling() {
    interval(3000).pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.orderService.getTablesAwaitingPayment()),
      catchError(() => of(this.tablesAwaitingPayment()))
    ).subscribe(tables => this.tablesAwaitingPayment.set(tables));
  }

  startRecentPaymentsPolling() {
    interval(3000).pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.paymentService.getRecentPayments()),
      catchError(() => of(this.recentPayments()))
    ).subscribe(payments => this.recentPayments.set(payments));
  }

  closeTable(tableNumber: number) {
    this.tablesAwaitingPayment.update(list => list.filter(n => n !== tableNumber));
    this.orderService.closeTable(tableNumber).subscribe({
      error: (err) => console.error("Error closing table:", err)
    });
  }

  goBack() {
    this.router.navigate(['/admin/dashboard']);
  }
}
