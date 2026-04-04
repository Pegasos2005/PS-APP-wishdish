import { Component } from '@angular/core';
import { OrderService } from '../services/order';
import { ProductCard } from '../menu/product-card/product-card';
import { DATA_MENU } from '../menu/menu.mock';

@Component({
  selector: 'app-order-list',
  imports: [ProductCard],
  templateUrl: './order-list.html',
  styleUrl: './order-list.css',
})
export class OrderList {
  constructor(public orderService: OrderService) {}

  protected readonly menuCategories = DATA_MENU;
}
