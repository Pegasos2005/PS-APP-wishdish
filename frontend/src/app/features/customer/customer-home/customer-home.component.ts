// src/app/features/customer/customer-home/customer-home.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-home.component.html',
  styleUrls: ['./customer-home.component.css']
})
export class CustomerHomeComponent {
  private router = inject(Router);

  // Signal para el nombre del restaurante (fácilmente editable en el futuro)
  restaurantName = signal<string>('WishDish');

  startOrdering(): void {
    console.log('Navegando al catálogo de menú');
    this.router.navigate(['/customer/customer-menu']);
  }

  watchOrders(): void {
    console.log('Navegando a la vista del ticket');
    this.router.navigate(['/customer/customer-ticket']);
  }
}
