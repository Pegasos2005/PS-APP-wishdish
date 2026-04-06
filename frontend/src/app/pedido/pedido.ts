import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ComandaService } from '../services/comanda.service';
import { ComandaResponseDTO } from '../models/comanda.model';
import { interval, Subscription } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-pedido',
  imports: [CommonModule],
  templateUrl: './pedido.html',
  styleUrl: './pedido.css',
  standalone: true
})
export class Pedido implements OnInit, OnDestroy {
  comandas = signal<ComandaResponseDTO[]>([]);
  errorConexion = signal(false);
  private pollingSubscription?: Subscription;

  constructor(
    private comandaService: ComandaService,
    private router: Router
  ) {}

  ngOnInit() {
    console.log('🔵 Pedido component initialized');
    this.cargarComandasActivas();
    this.iniciarPolling();
  }

  ngOnDestroy() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  cargarComandasActivas() {
    console.log('📤 Llamando a getComandasActivas...');
    this.comandaService.getComandasActivas()
      .pipe(
        catchError(error => {
          console.error('❌ Error al cargar comandas:', error);
          this.errorConexion.set(true);
          return of([]);
        })
      )
      .subscribe(comandas => {
        console.log('✅ Comandas recibidas:', comandas);
        this.comandas.set(comandas);
        this.errorConexion.set(false);
      });
  }

  iniciarPolling() {
    this.pollingSubscription = interval(3000)
      .pipe(
        switchMap(() => this.comandaService.getComandasActivas()),
        catchError(error => {
          console.error('Error en polling:', error);
          this.errorConexion.set(true);
          return of([]);
        })
      )
      .subscribe(comandas => {
        this.comandas.set(comandas);
        this.errorConexion.set(false);
      });
  }

  navigateToMenu() {
    this.router.navigate(['/']);
  }
}
