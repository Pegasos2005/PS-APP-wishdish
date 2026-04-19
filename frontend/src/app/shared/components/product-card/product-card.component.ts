// src/app/shared/components/product-card/product-card.component.ts
import { Component, Input, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
// OJO: Asegúrate de que el modelo esté en inglés según tus reglas (ProductDTO o Product)
import { CustomerOrderService } from '../../../core/services/customer-order.service';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css'],
})
export class ProductCardComponent {
  // En Angular 17+ podemos forzar a que el Input sea obligatorio
  @Input({ required: true }) product!: any; // Cambia 'any' por tu interfaz ProductDTO
  @Output() selectProduct = new EventEmitter<any>();

  private orderService = inject(CustomerOrderService);

  /* Click en el producto, abrir el modal */
  onProductClick() {
    this.selectProduct.emit(this.product);
  }

  onAddClick(event: Event) {
    event.stopPropagation();  // No abrir modal si se clickea en el +
    this.orderService.addProduct(this.product);
  }
}
