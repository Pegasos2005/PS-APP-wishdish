import { Injectable } from '@angular/core';
import { Product } from '../menu/product-card/product.interface';
import { OrderItem } from './orderItem.interface';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  order: OrderItem[] = [];
  // para el contador visual del carrito:
  totalItems: number = 0;

  constructor() {
  }

  addProduct(productToAdd: Product){

    try{

      const existingItem = this.order.find(item => item.product.id === productToAdd.id)

      // Si el producto está en la lista...
      // se suma 1 pero no se duplica el producto
      if(existingItem) {
        existingItem.quantity ++;
        console.log("Se ha incrementado: ", productToAdd.product_name, " en: ", existingItem.quantity, " unidad/es");
      }
      else{
        this.order.push({
          product: productToAdd,
          quantity: 1
        });
        console.log("Añadido nuevo plato: ", productToAdd.product_name);
      }

      this.totalItems++;

    } catch (error) {
      console.error("No se ha podido añadir el producto", error);
    }
  }

  decreaseProduct(productToRemove: Product){
    const index = this.order.findIndex(item => item.product.id === productToRemove.id);

    if(index != -1){
      this.order[index].quantity --;
      this.totalItems--;

      if(this.order[index].quantity === 0){
        this.order.splice(index, 1);
      }
    }
  }
}
