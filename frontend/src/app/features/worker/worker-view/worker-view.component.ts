import { Component, OnInit, signal, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { interval, of, forkJoin } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { KitchenOrdersSystemService } from '../../../core/services/kitchen-orders-system.service';
import { PaymentService } from '../../../core/services/payment.service';
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
  private paymentService = inject(PaymentService);
  private destroyRef = inject(DestroyRef);

  // Signal tipado con tu interfaz para evitar el error de DataTransferItemList
  orders = signal<ComandaResponseDTO[]>([]);

  // Cobros completados en los últimos minutos (aviso para el personal)
  recentPayments = signal<any[]>([]);

  private manualStates = new Map<number, string>();
  private isBulkUpdating = false;
  private excludedOrderIds = new Set<number>();

  ngOnInit() {
    this.startPolling();
    this.startRecentPaymentsPolling();
  }

  startRecentPaymentsPolling() {
    interval(3000).pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.paymentService.getRecentPayments()),
      catchError(() => of(this.recentPayments()))
    ).subscribe(payments => this.recentPayments.set(payments));
  }

  startPolling() {
    interval(3000).pipe(
      takeUntilDestroyed(this.destroyRef),
      switchMap(() => this.isBulkUpdating ? of(null) : this.kitchenService.getComandasActivas()),
      map((data: any) => {
        if (!data) return this.orders();
        
        const serverOrders = data
          .filter((order: any) => !this.excludedOrderIds.has(Number(order.id)))
          .map((order: any): ComandaResponseDTO => {
            const existingOrder = this.orders().find(o => Number(o.id) === Number(order.id));

            return {
              id: order.id,
              tableNumber: order.tableNumber,
              status: order.status,

              generalNotes: order.generalNotes,

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

        // Mantenemos las comandas que están saliendo (animándose) 
        // aunque ya no vengan en la respuesta del servidor
        const exitingOrders = this.orders().filter(o => o.isExiting);
        
        const finalOrders = [...serverOrders];
        exitingOrders.forEach(ex => {
          if (!finalOrders.some(o => Number(o.id) === Number(ex.id))) {
            finalOrders.push(ex);
          }
        });

        return finalOrders;
      }),
      catchError(() => of(this.orders()))
    ).subscribe(res => {
      if (res) this.orders.set(res);
    });
  }

  toggleItemStatus(order: ComandaResponseDTO, item: ItemComandaDTO) {
    const originalStatus = item.status;
    const newStatus = (originalStatus === 'prepared') ? 'in_kitchen' : 'prepared';

    // Actualización optimista de la UI
    item.status = newStatus;
    this.manualStates.set(item.id, newStatus);

    const isNowComplete = newStatus === 'prepared' && this.isOrderComplete(order);

    // Requisito: Si se completa la comanda al marcar este producto, preguntar confirmación
    if (isNowComplete) {
      const confirmed = window.confirm("¿Todos los ítems de esta comanda están preparados? ¿Desea finalizar la comanda?");
      if (!confirmed) {
        // Requisito: Si no confirma, se desmarca el último producto
        item.status = originalStatus;
        this.manualStates.set(item.id, originalStatus);
        return;
      }
    }

    this.kitchenService.avanzarEstadoItem(item.id, newStatus).subscribe({
      next: () => {
        if (isNowComplete) {
          // Solo disparamos la animación. El backend ya puso la comanda en 'served' 
          // y desaparecerá en el próximo polling.
          this.animateAndRemoveOrder(order, false); 
        }
      },
      error: (err) => {
        console.error("Error actualizando el estado del ítem:", err);
        item.status = originalStatus;
        this.manualStates.set(item.id, originalStatus);
      }
    });
  }

  private animateAndRemoveOrder(order: ComandaResponseDTO, callBackend: boolean = true) {
    if (order.isExiting) return;
    
    // Forzamos la actualización del signal para que Angular detecte el cambio de clase inmediatamente
    this.orders.update(current => {
      const target = current.find(o => Number(o.id) === Number(order.id));
      if (target) target.isExiting = true;
      return [...current];
    });

    // Solo llamamos al backend si es necesario (ej: botón "Completar todo")
    if (callBackend) {
      this.kitchenService.finalizarComanda(order.id).subscribe();
    }

    setTimeout(() => {
      const numericId = Number(order.id);
      this.excludedOrderIds.add(numericId);
      this.orders.update(current => current.filter(c => Number(c.id) !== numericId));
    }, 550); // 50ms extra para asegurar que la transición CSS termine
  }

  isOrderComplete(order: ComandaResponseDTO): boolean {
    if (!order.items || order.items.length === 0) return false;
    return order.items.every((i: ItemComandaDTO) => i.status === 'prepared');
  }

  toggleOrderComplete(order: ComandaResponseDTO) {
    const confirmed = window.confirm("¿Desea marcar todos los ítems de esta comanda como preparados y finalizarla?");
    if (!confirmed) return;

    this.isBulkUpdating = true;
    const originalStates = order.items.map(i => ({ id: i.id, status: i.status }));

    const updateObservables = order.items.map((item: ItemComandaDTO) => {
      item.status = 'prepared';
      this.manualStates.set(item.id, 'prepared');
      return this.kitchenService.avanzarEstadoItem(item.id, 'prepared');
    });

    forkJoin(updateObservables).subscribe({
      next: () => {
        this.animateAndRemoveOrder(order);
        this.isBulkUpdating = false;
      },
      error: (err) => {
        console.error("Error al actualizar masivamente los ítems de la comanda:", err);
        this.isBulkUpdating = false;
        originalStates.forEach(old => {
          const item = order.items.find(i => i.id === old.id);
          if (item) {
            item.status = old.status;
            this.manualStates.set(item.id, old.status);
          }
        });
      }
    });
  }
}
