import { Component } from '@angular/core';
import { OrderService } from '../services/order';
import { ProductCard } from '../menu/product-card/product-card';
import { DATA_MENU } from '../menu/menu.mock';
import { Product } from '../menu/product-card/product.interface';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-order-list',
  imports: [ProductCard, RouterLink],
  templateUrl: './order-list.html',
  styleUrl: './order-list.css',
})
export class OrderList {
  constructor(public orderService: OrderService) {}

  increment(product: Product) {
    this.orderService.addProduct(product);
  }

  decrement(product: Product) {
    this.orderService.decreaseProduct(product);
  }

  protected readonly menuCategories = DATA_MENU;
}
