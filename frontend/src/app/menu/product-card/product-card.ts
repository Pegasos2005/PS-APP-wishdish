import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Product } from './product.interface';

@Component({
  selector: 'app-product-card',
  imports: [],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})

export class ProductCard {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();

  // Para que se incremente el contador cuando le damos al boton +
  onAddClick(){
    this.addToCart.emit(this.product);
  }
}
