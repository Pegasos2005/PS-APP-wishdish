// src/app/features/customer/menu/customer-menu.component.ts
import { Component, OnInit, signal, ViewChild, ElementRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MenuService } from '../../../core/services/menu.service';
import { CustomerOrderService } from '../../../core/services/customer-order.service';
import { ProductCardComponent } from '../../../shared/components/product-card/product-card.component';
import {CustomerDishDetailComponent} from '../customer-dish-detail/customer-dish-detail.component';

@Component({
  selector: 'app-customer-menu',
  standalone: true,
  imports: [CommonModule, RouterLink, ProductCardComponent, CustomerDishDetailComponent],
  templateUrl: './customer-menu.component.html',
  styleUrls: ['./customer-menu.component.css']
})
export class CustomerMenuComponent implements OnInit {
  @ViewChild('productColumn') productColumn!: ElementRef;

  // Inyecciones modernas
  private menuService = inject(MenuService);
  public orderService = inject(CustomerOrderService); // Público para usarlo en el HTML

  menuCategories = signal<any[]>([]);
  selectedCategory: number | null = null;
  selectedProduct = signal<any>(null); // para mostrar el modal

  private isManualScroll = false;


  ngOnInit(): void {
    this.loadMenu();
  }

  loadMenu(): void {
    this.menuService.getMenu().subscribe({
      next: (data) => {
        this.menuCategories.set(data);
        if (data.length > 0) {
          this.selectedCategory = data[0].categoryId;
        }
      },
      error: (err) => console.error('Error loading menu', err)
    });
  }

  // Cuando se clickea un producto
  onProductSelected(product: any) {
    console.log('Producto seleccionado:', product);
    this.selectedProduct.set(product);
  }

  // Cuando se confirma el producto con ingredientes
  onAddToCart(productWithIngredients: any) {
    console.log('Añadiendo al carrito con ingredientes:', productWithIngredients);

    // Añadir al carrito
    this.orderService.addProduct(productWithIngredients);

    // Cerrar modal
    this.selectedProduct.set(null);
  }

  // Cerrar modal
  closeProductDetails() {
    this.selectedProduct.set(null);
  }

  scrollToCategory(categoryId: number): void {
    this.isManualScroll = true;
    this.selectedCategory = categoryId;
    const element = document.querySelector(`section[data-category-id="${categoryId}"]`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
    setTimeout(() => { this.isManualScroll = false; }, 1000);
  }

  onScroll(event: Event): void {
    if (this.isManualScroll) return;
    const container = event.target as HTMLElement;
    const sections = container.querySelectorAll('section');
    let currentCategory: number | null = null;

    sections.forEach((section: any) => {
      const rect = section.getBoundingClientRect();
      const containerRect = container.getBoundingClientRect();
      if (rect.top <= containerRect.top + 50) {
        currentCategory = Number(section.getAttribute('data-category-id'));
      }
    });

    if (currentCategory) {
      this.selectedCategory = currentCategory;
    }
  }
}
