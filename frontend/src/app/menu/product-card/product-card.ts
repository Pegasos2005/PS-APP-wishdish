import { Component, Input } from '@angular/core';
import { Producto } from '../../models/producto.model';
import { OrderService } from '../../services/order';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  @Input() product!: Producto;

  constructor(private orderService: OrderService) {}

  onAddClick(){
    this.orderService.addProduct(this.product)
  }
}