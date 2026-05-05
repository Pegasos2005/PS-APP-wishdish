import { Component, OnInit, signal, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { interval, of } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { KitchenOrdersSystemService } from '../../../core/services/kitchen-orders-system.service';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { ComandaResponseDTO, ItemComandaDTO } from '../../../core/models/comanda.model';

@Component({
  selector: 'app-worker-view',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './worker-view.component.html',
  styleUrls: ['./worker-view.component.css']
})
export class WorkerViewComponent implements OnInit {
  private kitchenService = inject(KitchenOrdersSystemService);
  private orderService = inject(CustomerOrderService);
  private destroyRef = inject(DestroyRef);

  // Signal tipado con tu interfaz para evitar el error de DataTransferItemList
  orders = signal<ComandaResponseDTO[]>([]);
  tablesAwaitingPayment = signal<number[]>([]);

  private manualStates = new Map<number, string>();
  private isBulkUpdating = false;
  private excludedOrderIds = new Set<number>();

  ngOnInit() {
    this.startPolling();
    this.startPaymentRequestsPolling();
  }

  startPolling() {
    interval(3000).pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.isBulkUpdating ? of(null) : this.kitchenService.getComandasActivas()),
      map((data: any) => {
        if (!data) return this.orders();

        return data
          .filter((order: any) => !this.excludedOrderIds.has(order.id))
          .map((order: any): ComandaResponseDTO => {
            const existingOrder = this.orders().find(o => o.id === order.id);

            return {
              id: order.id,
              tableNumber: order.tableNumber,
              status: order.status,
              isExiting: existingOrder ? existingOrder.isExiting : false,
              // Mapeamos los platos asegurando que lleguen todas las notas y cantidades
              items: (order.items || []).map((item: any): ItemComandaDTO => {
                const savedStatus = this.manualStates.get(item.id);
                return {
                  id: item.id,
                  status: savedStatus ? savedStatus : item.status,
                  productName: item.productName,
                  quantity: item.quantity,       // <-- Importante para el HTML
                  observations: item.observations, // <-- Los "Extras/Sin"
                  itemNotes: item.itemNotes || ''  // <-- Nota del cliente
                };
              })
            };
          });
      }),
      catchError(() => of(this.orders()))
    ).subscribe(res => {
      if (res) this.orders.set(res);
    });
  }

  toggleItemStatus(order: ComandaResponseDTO, item: ItemComandaDTO) {
    const newStatus = (item.status === 'prepared') ? 'in_kitchen' : 'prepared';
    this.manualStates.set(item.id, newStatus);
    item.status = newStatus;

    if (this.isOrderComplete(order)) {
      this.animateAndRemoveOrder(order);
    }

    this.kitchenService.avanzarEstadoItem(item.id).subscribe({
      next: () => {
        setTimeout(() => this.manualStates.delete(item.id), 7000);
      },
      error: () => {
        this.excludedOrderIds.delete(order.id);
        order.isExiting = false;
        this.manualStates.delete(item.id);
        item.status = (newStatus === 'prepared') ? 'in_kitchen' : 'prepared';
      }
    });
  }

  private animateAndRemoveOrder(order: ComandaResponseDTO) {
    if (order.isExiting) return;

    this.excludedOrderIds.add(order.id);
    order.isExiting = true;

    this.kitchenService.finalizarComanda(order.id).subscribe();

    setTimeout(() => {
      this.orders.update(current => current.filter(c => c.id !== order.id));
    }, 500);
  }

  isOrderComplete(order: ComandaResponseDTO): boolean {
    if (!order.items || order.items.length === 0) return false;
    return order.items.every((i: ItemComandaDTO) => i.status === 'prepared');
  }

  startPaymentRequestsPolling() {
    interval(3000).pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.orderService.getTablesAwaitingPayment()),
      catchError(() => of(this.tablesAwaitingPayment()))
    ).subscribe(tables => this.tablesAwaitingPayment.set(tables));
  }

  chargeTable(tableNumber: number) {
    this.tablesAwaitingPayment.update(list => list.filter(n => n !== tableNumber));
    this.orderService.closeTable(tableNumber).subscribe({
      error: (err) => console.error("Error closing table:", err)
    });
  }

  toggleOrderComplete(order: ComandaResponseDTO) {
    this.isBulkUpdating = true;
    if (!this.isOrderComplete(order)) {
      order.items.forEach((item: ItemComandaDTO) => {
        item.status = 'prepared';
        this.manualStates.set(item.id, 'prepared');
        this.kitchenService.avanzarEstadoItem(item.id).subscribe();
      });
      this.animateAndRemoveOrder(order);
    }
    setTimeout(() => this.isBulkUpdating = false, 4000);
  }
}
