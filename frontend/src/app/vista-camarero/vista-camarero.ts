import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ComandaService } from '../services/comanda.service';
import { ComandaResponseDTO } from '../models/comanda.model';
import { timer, Subscription, of } from 'rxjs';
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

  constructor(private comandaService: ComandaService) {}

  ngOnInit() {
    this.startPolling();
  }

  ngOnDestroy() {
    this.pollingSub?.unsubscribe();
  }

  /**
   * Consulta al backend Ya y luego repite cada 3 segundos
   */
  startPolling() {
    this.pollingSub = timer(0, 3000)
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
  marcarPlatoPreparado(comanda: ComandaResponseDTO, item: any) {

    this.comandaService.avanzarEstadoItem(item.id).subscribe({
      next: (itemActualizado) => {
        console.log("Item marcado como preparado en BD:", itemActualizado);

        item.status = 'prepared';

        if(this.isComandaCompleta(comanda)){

          console.log("Todos los items estan preparados. Comanda cmabiará a served");
          // EL backend ya cambió la comanda a 'served' automaticamente pero
          // actualizamos localmente para que el polling no tarde 3 segundos
          comanda.estado = 'served';
        }
      },
      error: (err) => {
        console.error("Error al marcar plato", err);
        alert("Error al actualizar el estado del plato");
      }
    });
  }

  /**
   * Determina visualmente si todos los platos están en estado 'preparado'
   */
  isComandaCompleta(comanda: ComandaResponseDTO): boolean {
    if (!comanda.items || comanda.items.length === 0) return false;
    return comanda.items.every((item: any) => item.status === 'prepared');
  }

  /**
   * Lógica del Master Check: Completa todo o restaura el estado anterior
   */
  toggleComandaCompleta(comanda: ComandaResponseDTO) {
    const yaEstaCompleta = this.isComandaCompleta(comanda);

    if (!yaEstaCompleta) {
      // Marcar TODOS LOS ITEMS uno por uno
      comanda.items.forEach((item: any) => {
        if (item.status !== 'prepared') {
          this.marcarPlatoPreparado(comanda, item);  // ← Avisa al backend por cada uno
        }
      });
    }
    else {
      // si ya esta completa
      console.log("La comanda ya está completa")
    }

  }

  /**
   * Envía la señal al backend para avanzar el estado de la comanda
   */
  finalizarComanda(comanda: any) {
    this.comandaService.updateComandaStatus(comanda.id, 'servida').subscribe({
      next: (response) => {
        console.log('✅ Comanda marcada como servida en BD:', response);
        comanda.estado = 'servida';
        // El polling recargaráy desaparecerá de la lista automáticamente
      },
      error: (err) => {
        console.error('❌ Error al actualizar comanda:', err);
      }
    });
  }

  /**
   * Comprueba si hay alguna comanda que no esté servida
   */
  hayComandasPendientes(): boolean {
    // Aquí TypeScript sí nos deja usar la flechita sin quejarse
    return this.comandas().some(c => c.estado !== 'served');
  }
}
