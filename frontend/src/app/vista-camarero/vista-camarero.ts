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

  constructor(private comandaService: ComandaService) {}

  ngOnInit() {
    this.startPolling();
  }

  ngOnDestroy() {
    this.pollingSub?.unsubscribe();
  }

  startPolling() {
    this.pollingSub = interval(3000)
      .pipe(
        switchMap(() => this.isBulkUpdating ? of(null) : this.comandaService.getComandasActivas()),
        map((data: any) => {
          if (!data) return this.comandas();

          return data.map((order: any) => ({
            id: order.id,
            numeroMesa: order.tableNumber,
            status: order.status,
            items: (order.items || []).map((item: any) => {
              const savedStatus = this.manualStates.get(item.id);
              // Sincronizamos con el nombre exacto del DTO: productName
              return {
                id: item.id,
                status: savedStatus ? savedStatus : item.status,
                nombrePlato: item.productName,
                notas: item.itemNotes || ''
              };
            })
          }));
        }),
        catchError((error) => {
          console.error("Error conectando con el backend:", error);
          return of(this.comandas());
        })
      )
      .subscribe(res => this.comandas.set(res));
  }

  marcarPlatoPreparado(comanda: any, item: any) {
    const nuevoEstado = (item.status === 'prepared') ? 'in_kitchen' : 'prepared';

    this.manualStates.set(item.id, nuevoEstado);
    item.status = nuevoEstado;

    this.comandaService.avanzarEstadoItem(item.id).subscribe({
      next: () => {
        setTimeout(() => this.manualStates.delete(item.id), 7000);
      },
      error: () => {
        this.manualStates.delete(item.id);
        item.status = (nuevoEstado === 'prepared') ? 'in_kitchen' : 'prepared';
        alert("No se pudo conectar con el servidor para actualizar el plato");
      }
    });
  }

  isOrderComplete(comanda: any): boolean {
    if (!comanda.items || comanda.items.length === 0) return false;
    return comanda.items.every((i: any) => i.status === 'prepared');
  }

  toggleComandaCompleta(comanda: any) {
    this.isBulkUpdating = true;
    const target = this.isOrderComplete(comanda) ? 'in_kitchen' : 'prepared';

    comanda.items.forEach((item: any) => {
      if (item.status !== target) {
        this.marcarPlatoPreparado(comanda, item);
      }
    });

  }
}
