// src/app/features/customer/show-order/show-order.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CustomerOrderService } from '../../../core/services/customer-order.service';

@Component({
  selector: 'app-show-order',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './show-order.component.html',
  styleUrls: ['./show-order.component.css']
})
export class ShowOrderComponent {
  public orderService = inject(CustomerOrderService);
  private router = inject(Router);

  public tableId = 1;

  increaseQuantity(product: any) {
    this.orderService.addProduct(product);
  }

  decreaseQuantity(product: any) {
    this.orderService.decreaseProduct(product);
  }

  // --- NUEVA FUNCIÓN: Calcula el precio total del carrito ---
  getTotalPrice(): number {
    return this.orderService.order.reduce((total, item) => {
      return total + (item.product.price * item.quantity);
    }, 0);
  }

  private getProductIds(): number[] {
    const ids: number[] = [];
    this.orderService.order.forEach(item => {
      for (let i = 0; i < item.quantity; i++) {
        ids.push(item.product.id);
      }
    });
    return ids;
  }

  sendOrder() {
    if (this.orderService.order.length === 0) {
      alert("Your cart is empty. Please add some products.");
      return;
    }
    // Preparamos los items con sus extras y quitados
    const itemsPayload = this.orderService.order.map(item => ({
      productId: item.product.id,
      quantity: item.quantity,
      addedExtras: item.product.addedExtras || [],
      removedDefaults: item.product.removedDefaults || []
    }));

    const orderPayload = {
      tableId: this.tableId,
      productIds: itemsPayload
    };

    console.log("Sending to kitchen:", orderPayload);

    this.orderService.crearPedido(orderPayload).subscribe({
      next: () => {
        alert("Order sent to the kitchen successfully!");
        this.orderService.clear();
        this.router.navigate(['/customer/customer-home']);
      },
      error: (err) => {
        console.error("Error sending order:", err);
        alert("Could not connect to the server. Please check your connection.");
      }
    });
  }
}
