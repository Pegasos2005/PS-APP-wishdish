import { Component } from '@angular/core';
import { OrderService } from '../services/order';
import { Producto } from '../models/producto.model';
import { Router, RouterLink } from '@angular/router';
import { ComandaService } from '../services/comanda.service';

@Component({
  selector: 'app-order-list',
  imports: [RouterLink],
  templateUrl: './order-list.html',
  styleUrl: './order-list.css',
})
export class OrderList {
  constructor(
    public orderService: OrderService,
    private comandaService: ComandaService,
    private router: Router
  ) {}

  increment(product: Producto) {
    this.orderService.addProduct(product);
  }

  decrement(product: Producto) {
    this.orderService.decreaseProduct(product);
  }

  sendOrder() {
    if (this.orderService.totalItems() === 0) {
      alert('El carrito está vacío');
      return;
    }

    const productosIds = this.orderService.order.flatMap(item =>
      Array(item.quantity).fill(item.product.id)
    );

    const comandaRequest = {
      numeroMesa: 1,
      productosIds
    };

    this.comandaService.crearComanda(comandaRequest).subscribe({
      next: (comanda: any) => {
        console.log('Comanda creada:', comanda);
        alert(`Comanda creada exitosamente para mesa ${comandaRequest.numeroMesa}`);
        this.orderService.clear();
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        console.error('Error al crear comanda:', error);

        let errorMessage = 'Error al crear la comanda';
        if (error.status === 0) {
          errorMessage = 'No se puede conectar con el servidor. Verifica que el backend esté corriendo en http://localhost:8080';
        } else if (error.error?.message) {
          errorMessage = `Error: ${error.error.message}`;
        } else if (error.message) {
          errorMessage = error.message;
        }

        alert(errorMessage);
      }
    });
  }
}
