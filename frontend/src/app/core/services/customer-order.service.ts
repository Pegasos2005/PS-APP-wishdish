import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto.model';
import { OrderItem } from '../interfaces/order-item.interface'; // Fíjate en el nuevo nombre
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CustomerOrderService {
  private apiUrl = environment.apiUrl + 'orders';

  order: OrderItem[] = [];
  private _totalItems = signal(0);
  totalItems = computed(() => this._totalItems());

  constructor(private http: HttpClient) {}

  getTicketByTable(tableId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/table/${tableId}`);
  }

  // 1. EL BOTÓN DE PAGAR (Envía a la base de datos)
  crearPedido(pedido: any): Observable<any> {
    return this.http.post(this.apiUrl, pedido);
  }

  // 2. GESTIÓN DEL CARRITO EN MEMORIA
  addProduct(productToAdd: Producto) {
    const existingItem = this.order.find(item => item.product.id === productToAdd.id);
    if (existingItem) {
      existingItem.quantity++;
    } else {
      this.order.push({ product: productToAdd, quantity: 1 });
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

  getSelectedProducts(): OrderItem[] {
    return this.order;
  }
}
