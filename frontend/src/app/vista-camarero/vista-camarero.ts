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
  comandas = signal<any[]>([]); // Usamos any para los mocks
  private pollingSub?: Subscription;
  private memoriaEstados = new Map<number, string[]>();

  constructor(private comandaService: ComandaService) {}

  ngOnInit() {
    // 1. Cargamos datos de prueba inmediatamente
    this.cargarMocks();

    // 2. Comentamos el polling real para que no sobreescriba los mocks con errores del server
    // this.startPolling();
  }

  ngOnDestroy() {
    this.pollingSub?.unsubscribe();
  }

  /**
   * DATOS MOCK para pruebas de interfaz
   */
  cargarMocks() {
    const mockData = [
      {
        id: 1,
        numeroMesa: 5,
        estado: 'pendiente',
        items: [
          { id: 101, producto: { nombre: 'Hamburguesa Especial' }, estado: 'en_cocina', itemNotes: 'Sin cebolla' },
          { id: 102, producto: { nombre: 'Patatas Bravas' }, estado: 'preparado', itemNotes: '' }
        ]
      },
      {
        id: 2,
        numeroMesa: 12,
        estado: 'pendiente',
        items: [
          { id: 201, producto: { nombre: 'Pizza Carbonara' }, estado: 'en_cocina', itemNotes: 'Extra de queso' },
          { id: 202, producto: { nombre: 'Coca-Cola' }, estado: 'en_cocina', itemNotes: '' }
        ]
      }
    ];
    this.comandas.set(mockData);
  }

  startPolling() {
    this.pollingSub = interval(3000)
      .pipe(
        switchMap(() => this.comandaService.getComandasActivas()),
        catchError((error) => {
          console.error("Error conectando con el servidor:", error);
          return of([]);
        })
      )
      .subscribe(data => {
        this.comandas.set(data);
      });
  }

  marcarPlatoPreparado(comanda: any, item: any) {
    item.estado = (item.estado === 'preparado') ? 'en_cocina' : 'preparado';
    this.memoriaEstados.delete(comanda.id);

    if (this.isComandaCompleta(comanda)) {
      this.finalizarComanda(comanda);
    } else {
      comanda.estado = 'pendiente';
    }
  }

  isComandaCompleta(comanda: any): boolean {
    if (!comanda.items || comanda.items.length === 0) return false;
    return comanda.items.every((item: any) => item.estado === 'preparado');
  }

  toggleComandaCompleta(comanda: any) {
    const yaEstaCompleta = this.isComandaCompleta(comanda);

    if (!yaEstaCompleta) {
      const instantanea = comanda.items.map((i: any) => i.estado);
      this.memoriaEstados.set(comanda.id, instantanea);
      comanda.items.forEach((item: any) => item.estado = 'preparado');
      this.finalizarComanda(comanda);
    } else {
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

  finalizarComanda(comanda: any) {
    comanda.estado = 'servida';
    // Solo intentamos llamar al servicio si no estamos en modo manual/mock completo
    if (comanda.id > 1000) { // Un check tonto para no llamar al server con mocks
      this.comandaService.avanzarEstadoItem(comanda.id).subscribe();
    }
  }
}
