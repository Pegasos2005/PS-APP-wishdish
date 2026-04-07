import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ComandaService } from '../services/comanda.service';
import { ComandaResponseDTO } from '../models/comanda.model';
import { interval, Subscription, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-vista-camarero',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './vista-camarero.html',
  styleUrl: './vista-camarero.css'
})
export class VistaCamarero implements OnInit, OnDestroy {
  comandas = signal<ComandaResponseDTO[]>([]);
  private pollingSub?: Subscription;

  // Memoria para restaurar estados previos al desmarcar el Master Check
  private memoriaEstados = new Map<number, string[]>();

  constructor(private comandaService: ComandaService) {}

  ngOnInit() {
    this.startPolling();
  }

  ngOnDestroy() {
    this.pollingSub?.unsubscribe();
  }

  /**
   * Consulta al backend cada 3 segundos las comandas activas
   */
  startPolling() {
    this.pollingSub = interval(3000)
      .pipe(
        switchMap(() => this.comandaService.getComandasActivas()),
        catchError((error) => {
          console.error("Error conectando con el servidor:", error);
          return of([]); // Si falla, devuelve array vacío para no romper la vista
        })
      )
      .subscribe(data => {
        this.comandas.set(data);
      });
  }

  /**
   * Marca un plato individualmente y verifica si la comanda se completa
   */
  marcarPlatoPreparado(comanda: any, item: any) {
    item.estado = (item.estado === 'preparado') ? 'en_cocina' : 'preparado';

    // Si se modifica manualmente, invalidamos la memoria de restauración
    this.memoriaEstados.delete(comanda.id);

    if (this.isComandaCompleta(comanda)) {
      this.finalizarComanda(comanda);
    }
  }

  /**
   * Determina visualmente si todos los platos están en estado 'preparado'
   */
  isComandaCompleta(comanda: any): boolean {
    if (!comanda.items || comanda.items.length === 0) return false;
    return comanda.items.every((item: any) => item.estado === 'preparado');
  }

  /**
   * Lógica del Master Check: Completa todo o restaura el estado anterior
   */
  toggleComandaCompleta(comanda: any) {
    const yaEstaCompleta = this.isComandaCompleta(comanda);

    if (!yaEstaCompleta) {
      // Guardar instantánea antes de marcar todo
      const instantanea = comanda.items.map((i: any) => i.estado);
      this.memoriaEstados.set(comanda.id, instantanea);

      // Marcar todos como preparados
      comanda.items.forEach((item: any) => item.estado = 'preparado');
      this.finalizarComanda(comanda);
    } else {
      // Restaurar desde memoria
      const estadoAnterior = this.memoriaEstados.get(comanda.id);

      if (estadoAnterior) {
        comanda.items.forEach((item: any, index: number) => {
          item.estado = estadoAnterior[index];
        });
      } else {
        comanda.items.forEach((item: any) => item.estado = 'en_cocina');
      }
      comanda.estado = 'pendiente';
    }
  }

  /**
   * Envía la señal al backend para avanzar el estado de la comanda
   */
  finalizarComanda(comanda: any) {
    comanda.estado = 'servida';
    this.comandaService.avanzarEstadoItem(comanda.id).pipe(
      catchError((err) => {
        console.error("No se pudo actualizar el estado en el servidor", err);
        return of(null);
      })
    ).subscribe();
  }
}
