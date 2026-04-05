import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCard } from './product-card/product-card';
import { MenuService } from '../services/menu.service';
import { MenuCategoria } from '../models/menu-categoria.model';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, ProductCard],
  templateUrl: './menu.html',
  styleUrl: './menu.css',
})
export class Menu implements OnInit {
  menuCategories = signal<MenuCategoria[]>([]);
  selectedCategory: number | null = null;
  cartCount: number = 0;
  isLoading = signal<boolean>(false);

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.loadMenu();
  }

  /**
   * Carga el menú desde el backend
   */
  private loadMenu(): void {
    this.isLoading.set(true);

    this.menuService.getMenu().subscribe({
      next: (data) => {
        this.menuCategories.set(data);
        this.isLoading.set(false);
        console.log('✅ Menú cargado correctamente', data);
      },
      error: (error) => {
        console.error('❌ Error al cargar el menú:', error);
        this.isLoading.set(false);
      }
    });
  }

  onAddToCart(product: any) {
    this.cartCount++;
    console.log(`Añadiste ${product.nombre} al carrito`);
  }

  scrollToCategory(categoryId: number) {
    this.selectedCategory = categoryId;
    const element = document.querySelector(
      `[data-category-id="${categoryId}"]`
    ) as HTMLElement;

    if(element) {
      element.scrollIntoView({ behavior: 'smooth'});
    }
  }

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
