import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';           // Importante para que funcionen cosas básicas
import { DATA_MENU } from './menu.mock';                  // Importar el json
import { ProductCard } from './product-card/product-card';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, ProductCard],
  templateUrl: './menu.html',
  styleUrl: './menu.css',
})
export class Menu {
  menuCategories = DATA_MENU;
  selectedCategory: number | null=null;
  cartCount: number = 0;

  onAddToCart(product: any) {
    this.cartCount++;
    console.log(`Añadiste ${product.product_name} al carrito`);
  }

  // Cuando se hace click en un botón de categoría
  scrollToCategory(categoryId: number) {
    this.selectedCategory = categoryId;

    // Para buscar el elemento section de esa categoría
    const element = document.querySelector(
      `[data-category-id="${categoryId}"]`
    ) as HTMLElement;

    if(element) {
      element.scrollIntoView({ behavior: 'smooth'});
    }
  }

  // Al hacer scroll en la derecha
  onScroll(event: Event){
    const container = event.target as HTMLElement;

    const categories = document.querySelectorAll('.section-title');

    for (let section of categories) {
      const rect = section.getBoundingClientRect();
      if (rect.top < 300) {
        const categoryId = (section as HTMLElement).getAttribute('data-category-id');
        if(categoryId){
          this.selectedCategory = parseInt(categoryId);
        }
      }
    }
  }
}
