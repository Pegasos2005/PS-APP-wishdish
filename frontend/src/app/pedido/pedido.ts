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
  templateUrl: './pedido.html',
  styleUrl: './pedido.css'
})
export class Pedido implements OnInit, OnDestroy {
  // Inputs para recibir datos de componentes padres
  @Input() productosSeleccionados: any[] = [];
  @Input() mesaId: number = 1;
  @Input() isMenuView: boolean = false;

  // Propiedades vinculadas a la vista
  carrito: any[] = [];
  numeroMesa: number = 1;
  notasPedido: string = '';

  // Señales para el estado de las comandas (Polling)
  comandas = signal<ComandaResponseDTO[]>([]);
  errorConexion = signal(false);
  private pollingSubscription?: Subscription;

  constructor(
    private comandaService: ComandaService,
    public orderService: OrderService, // public para acceder desde el HTML
    private router: Router
  ) {}

  ngOnInit() {
    // Sincronizamos la mesa con el valor recibido
    this.numeroMesa = this.mesaId;

    // Sincronizamos el carrito local con la "fuente de verdad" del servicio
    this.carrito = this.orderService.order;

    if (!this.isMenuView) {
      this.cargarComandasActivas();
      this.iniciarPolling();
    }
  }

  ngOnDestroy() {
    // Evitamos fugas de memoria cancelando el intervalo al destruir el componente
    this.pollingSubscription?.unsubscribe();
  }

  /**
   * Procesa el envío del pedido al Backend (Spring Boot)
   */
  enviarComanda() {
    console.log("Iniciando proceso de envío...");

    // 1. Verificación de seguridad: ¿Hay algo que enviar?
    if (this.orderService.order.length === 0) {
      alert("El carrito está vacío. Por favor, añade productos.");
      return;
    }

    // 2. Mapeo de datos: Ajustado a OrderRequestDTO de Java
    // Java espera: { tableId: Integer, productIds: List<Integer> }
    const pedidoParaEnviar = {
      tableId: this.numeroMesa,
      productIds: this.orderService.order.map(item => item.product.id)
    };

    console.log("JSON enviado al servidor:", pedidoParaEnviar);

    // 3. Llamada al servicio HTTP
    this.orderService.crearPedido(pedidoParaEnviar).subscribe({
      next: (res: any) => {
        console.log("Éxito en el servidor:", res);
        alert("¡Pedido enviado correctamente! Volviendo al menú...");

        // 4. Limpieza y navegación tras éxito
        this.finalizarYRegresar();
      },
      error: (err: any) => {
        console.error("Error detallado en el envío:", err);
        // Posibles causas: CORS mal configurado en Java o URL incorrecta
        alert("No se pudo conectar con el servidor. Revisa la consola (F12).");
      }
    });
  }

  /**
   * Limpia el estado de la aplicación y redirige al usuario
   */
  private finalizarYRegresar() {
    // Vacía el array 'order' y resetea 'totalItems' en el servicio
    this.orderService.clear();

    // Resetea variables locales
    this.carrito = [];
    this.notasPedido = '';

    // Navega a la ruta raíz (Menú)
    this.router.navigate(['/']);
  }

  /**
   * Carga inicial de las comandas activas para la vista de seguimiento
   */
  cargarComandasActivas() {
    this.comandaService.getComandasActivas()
      .pipe(
        catchError((err) => {
          console.warn("Fallo al obtener comandas activas:", err);
          this.errorConexion.set(true);
          return of([]);
        })
      )
      .subscribe(comandas => {
        this.comandas.set(comandas);
        this.errorConexion.set(false);
      });
  }

  /**
   * Actualiza la lista de comandas cada 3 segundos
   */
  iniciarPolling() {
    this.pollingSubscription = interval(3000)
      .pipe(
        switchMap(() => this.comandaService.getComandasActivas()),
        catchError(() => of([]))
      )
      .subscribe(comandas => {
        this.comandas.set(comandas);
      });
  }

  /**
   * Método manual para volver atrás sin enviar nada
   */
  navigateToMenu() {
    this.router.navigate(['/']);
  }
}
