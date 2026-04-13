import { Component, OnInit, signal, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { interval, of } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { KitchenOrdersSystemService } from '../../../core/services/kitchen-orders-system.service';

@Component({
  selector: 'app-worker-view',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './worker-view.component.html',
  styleUrls: ['./worker-view.component.css'] // <-- Ojo, styleUrls con 's'
})
export class WorkerViewComponent implements OnInit {
  private kitchenService = inject(KitchenOrdersSystemService);
  private destroyRef = inject(DestroyRef); // Nueva forma de destruir suscripciones

  // Variables traducidas al inglés
  orders = signal<any[]>([]);
  private manualStates = new Map<number, string>();
  private isBulkUpdating = false;
  private excludedOrderIds = new Set<number>();

  ngOnInit() {
    this.startPolling();
  }

  startPolling() {
    interval(3000).pipe(
      // Se detiene automáticamente cuando el componente se destruye (al salir de la vista)
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.isBulkUpdating ? of(null) : this.kitchenService.getComandasActivas()),
      map((data: any) => {
        if (!data) return this.orders();

        return data
          .filter((order: any) => !this.excludedOrderIds.has(order.id))
          .map((order: any) => ({
            id: order.id,
            tableNumber: order.tableNumber, // Traducido
            status: order.status,
            isExiting: false,
            items: (order.items || []).map((item: any) => {
              const savedStatus = this.manualStates.get(item.id);
              return {
                id: item.id,
                status: savedStatus ? savedStatus : item.status,
                productName: item.productName, // Traducido
                notes: item.itemNotes || ''    // Traducido
              };
            })
          }));
      }),
      catchError(() => of(this.orders()))
    ).subscribe(res => this.orders.set(res));
  }

  toggleItemStatus(order: any, item: any) { // Antes marcarPlatoPreparado
    const newStatus = (item.status === 'prepared') ? 'in_kitchen' : 'prepared';
    this.manualStates.set(item.id, newStatus);
    item.status = newStatus;

    this.kitchenService.avanzarEstadoItem(item.id).subscribe({
      next: () => {
        if (this.isOrderComplete(order)) {
          this.animateAndRemoveOrder(order);
        }
        setTimeout(() => this.manualStates.delete(item.id), 7000);
      },
      error: () => {
        this.manualStates.delete(item.id);
        item.status = (newStatus === 'prepared') ? 'in_kitchen' : 'prepared';
      }
    });
  }

  private animateAndRemoveOrder(order: any) { // Antes animarYBorrarComanda
    this.excludedOrderIds.add(order.id);
    order.isExiting = true;

    this.kitchenService.finalizarComanda(order.id).subscribe();

    setTimeout(() => {
      this.orders.update(current => current.filter(c => c.id !== order.id));
    }, 500);
  }

  isOrderComplete(order: any): boolean {
    if (!order.items || order.items.length === 0) return false;
    return order.items.every((i: any) => i.status === 'prepared');
  }

  toggleOrderComplete(order: any) { // Antes toggleComandaCompleta
    this.isBulkUpdating = true;
    if (!this.isOrderComplete(order)) {
      order.items.forEach((item: any) => {
        item.status = 'prepared';
        this.manualStates.set(item.id, 'prepared');
        this.kitchenService.avanzarEstadoItem(item.id).subscribe();
      });
      this.animateAndRemoveOrder(order);
    }
    setTimeout(() => this.isBulkUpdating = false, 4000);
  }
}
