import { Injectable, signal, computed } from '@angular/core';
import { Producto } from '../models/producto.model';
import { OrderItem } from './orderItem.interface';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  order: OrderItem[] = [];
  private _totalItems = signal(0);
  totalItems = computed(() => this._totalItems());

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
}