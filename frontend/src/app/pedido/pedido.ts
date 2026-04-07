import { Component, OnInit, OnDestroy, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ComandaService } from '../services/comanda.service';
import { OrderService } from '../services/order';
import { ComandaResponseDTO } from '../models/comanda.model';
import { interval, Subscription, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedido.html', // Corregido: antes decía menu.html
  styleUrl: './pedido.css'
})
export class Pedido implements OnInit, OnDestroy {
  @Input() productosSeleccionados: number[] = [];
  @Input() mesaId: number = 1;
  @Input() isMenuView: boolean = false;

  comandas = signal<ComandaResponseDTO[]>([]);
  errorConexion = signal(false);
  private pollingSubscription?: Subscription;

  constructor(
    private comandaService: ComandaService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit() {
    if (!this.isMenuView) {
      this.cargarComandasActivas();
      this.iniciarPolling();
    }
  }

  ngOnDestroy() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  enviarComanda() {
    if (this.productosSeleccionados.length === 0) {
      alert("No has seleccionado ningún plato.");
      return;
    }

    const peticion = {
      numeroMesa: this.mesaId,
      productosIds: this.productosSeleccionados
    };

    this.comandaService.crearComanda(peticion).subscribe({
      next: (res) => {
        alert("¡Comanda enviada correctamente!");
        this.orderService.clear();
      },
      error: (err) => {
        console.error("Error:", err);
        alert("Fallo al conectar con el servidor.");
      }
    });
  }

  cargarComandasActivas() {
    this.comandaService.getComandasActivas()
      .pipe(catchError(() => {
        this.errorConexion.set(true);
        return of([]);
      }))
      .subscribe(comandas => {
        this.comandas.set(comandas);
        this.errorConexion.set(false);
      });
  }

  iniciarPolling() {
    this.pollingSubscription = interval(3000)
      .pipe(
        switchMap(() => this.comandaService.getComandasActivas()),
        catchError(() => of([]))
      )
      .subscribe(comandas => this.comandas.set(comandas));
  }

  navigateToMenu() {
    this.router.navigate(['/']);
  }
}
