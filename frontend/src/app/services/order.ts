import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // <--- IMPORTANTE
import { Observable } from 'rxjs'; // <--- IMPORTANTE
import { Producto } from '../models/producto.model';
import { OrderItem } from './orderItem.interface';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  // Ajusta esta URL a la de tu API de Spring Boot
  private apiUrl = 'http://localhost:8080/api/orders';

  order: OrderItem[] = [];
  private _totalItems = signal(0);
  totalItems = computed(() => this._totalItems());

  // Añadimos el HttpClient al constructor
  constructor(private http: HttpClient) {}

  /**
   * ESTE ES EL MÉTODO QUE TE FALTABA
   * Envía el objeto de la comanda al servidor
   */
  crearPedido(pedido: any): Observable<any> {
    return this.http.post(this.apiUrl, pedido);
  }

  // --- LÓGICA DE GESTIÓN DE CARRITO (YA LA TENÍAS) ---

  addProduct(productToAdd: Producto) {
    const existingItem = this.order.find(item => item.product.id === productToAdd.id);

    if (existingItem) {
      existingItem.quantity++;
    } else {
      this.order.push({
        product: productToAdd,
        quantity: 1
      });
    }

    this._totalItems.update(val => val + 1);
  }

  decreaseProduct(productToRemove: Producto) {
    const index = this.order.findIndex(item => item.product.id === productToRemove.id);

    if (index !== -1) {
      this.order[index].quantity--;
      this._totalItems.update(val => val - 1);

      if (this.order[index].quantity === 0) {
        this.order.splice(index, 1);
      }
    }
  }

  clear() {
    this.order = [];
    this._totalItems.set(0);
  }

  // Método útil para que el componente Pedido obtenga los productos actuales
  getSelectedProducts(): OrderItem[] {
    return this.order;
  }
}
