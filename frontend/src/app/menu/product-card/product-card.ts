import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  @Input() product!: Producto;
  @Output() addToCart = new EventEmitter<Producto>();

  onAddClick(){
    this.addToCart.emit(this.product);
  }
}
