import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../services/order';
import { Pedido } from '../pedido/pedido';
import { Producto } from '../models/producto.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink, Pedido],
  templateUrl: './order-list.html',
  styleUrl: './order-list.css'
})
export class OrderList {
  // Inyectamos el servicio para acceder al carrito
  constructor(public orderService: OrderService) {}

  // Lógica para el botón "+"
  increment(product: Producto) {
    this.orderService.addProduct(product);
  }

  // Lógica para el botón "-"
  decrement(product: Producto) {
    this.orderService.decreaseProduct(product);
  }

  // Esta función es la que genera la lista de IDs repetidos [12, 12, 12...]
  // Se la pasamos al componente <app-pedido> en el HTML
  obtenerIdsParaComanda(): number[] {
    const ids: number[] = [];
    this.orderService.order.forEach(item => {
      for (let i = 0; i < item.quantity; i++) {
        ids.push(item.product.id);
      }
    });
    return ids;
  }
}
