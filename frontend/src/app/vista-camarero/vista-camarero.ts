import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ComandaService } from '../services/comanda.service';
import { interval, Subscription, of } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';

@Component({
  selector: 'app-vista-camarero',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './vista-camarero.html',
  styleUrl: './vista-camarero.css'
})
export class VistaCamarero implements OnInit, OnDestroy {
  comandas = signal<any[]>([]);
  private pollingSub?: Subscription;
  private manualStates = new Map<number, string>();
  private isBulkUpdating = false;

  // ESCUDO DEFINITIVO: IDs de comandas que YA hemos finalizado y no queremos volver a ver
  private excludedOrderIds = new Set<number>();

  constructor(private comandaService: ComandaService) {}

  ngOnInit() { this.startPolling(); }
  ngOnDestroy() { this.pollingSub?.unsubscribe(); }

  startPolling() {
    this.pollingSub = interval(3000).pipe(
      switchMap(() => this.isBulkUpdating ? of(null) : this.comandaService.getComandasActivas()),
      map((data: any) => {
        if (!data) return this.comandas();

        // FILTRADO CRÍTICO: Si el ID está en la lista negra, lo eliminamos del resultado
        return data
          .filter((order: any) => !this.excludedOrderIds.has(order.id))
          .map((order: any) => ({
            id: order.id,
            numeroMesa: order.tableNumber,
            status: order.status,
            isExiting: false,
            items: (order.items || []).map((item: any) => {
              const savedStatus = this.manualStates.get(item.id);
              return {
                id: item.id,
                status: savedStatus ? savedStatus : item.status,
                nombrePlato: item.productName,
                notas: item.itemNotes || ''
              };
            })
          }));
      }),
      catchError(() => of(this.comandas()))
    ).subscribe(res => this.comandas.set(res));
  }

  marcarPlatoPreparado(comanda: any, item: any) {
    const nuevoEstado = (item.status === 'prepared') ? 'in_kitchen' : 'prepared';
    this.manualStates.set(item.id, nuevoEstado);
    item.status = nuevoEstado;

    this.comandaService.avanzarEstadoItem(item.id).subscribe({
      next: () => {
        if (this.isOrderComplete(comanda)) {
          this.animarYBorrarComanda(comanda);
        }
        setTimeout(() => this.manualStates.delete(item.id), 7000);
      },
      error: () => {
        this.manualStates.delete(item.id);
        item.status = (nuevoEstado === 'prepared') ? 'in_kitchen' : 'prepared';
      }
    });
  }

  private animarYBorrarComanda(comanda: any) {
    // 1. Añadir a la lista negra para que el polling NO la vuelva a pintar
    this.excludedOrderIds.add(comanda.id);

    // 2. Iniciar animación visual
    comanda.isExiting = true;

    // 3. Notificar al servidor
    this.comandaService.finalizarComanda(comanda.id).subscribe();

    // 4. EL POP: Eliminar del signal local tras la animación
    setTimeout(() => {
      this.comandas.update(current => current.filter(c => c.id !== comanda.id));
    }, 500);
  }

  isOrderComplete(comanda: any): boolean {
    if (!comanda.items || comanda.items.length === 0) return false;
    return comanda.items.every((i: any) => i.status === 'prepared');
  }

  toggleComandaCompleta(comanda: any) {
    this.isBulkUpdating = true;
    if (!this.isOrderComplete(comanda)) {
      comanda.items.forEach((item: any) => {
        item.status = 'prepared';
        this.manualStates.set(item.id, 'prepared');
        this.comandaService.avanzarEstadoItem(item.id).subscribe();
      });
      this.animarYBorrarComanda(comanda);
    }
    setTimeout(() => this.isBulkUpdating = false, 4000);
  }
}
