import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../services/order';
import { Pedido } from '../pedido/pedido';
import { Producto } from '../models/producto.model'; // Verifica que la ruta sea correcta

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

  // ESTA ES LA FUNCIÓN QUE TE DABA EL ERROR
  // Transforma el carrito en una lista de IDs para el componente Pedido
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
