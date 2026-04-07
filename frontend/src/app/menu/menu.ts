import { Component, OnInit, signal, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MenuService } from '../services/menu.service';
import { OrderService } from '../services/order';
import { Pedido } from '../pedido/pedido';

// IMPORTANTE: Asegúrate de que esta ruta y el nombre de la clase sean correctos
// Si la clase dentro del archivo se llama ProductCardComponent, cámbialo aquí abajo también
import { ProductCard } from './product-card/product-card';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, RouterModule, Pedido, ProductCard],
  templateUrl: './menu.html',
  styleUrl: './menu.css'
})
export class Menu implements OnInit {
  @ViewChild('productColumn') productColumn!: ElementRef;

  menuCategories = signal<any[]>([]);
  selectedCategory: number | null = null;
  private isManualScroll = false;

  constructor(
    private menuService: MenuService,
    public orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMenu();
  }

  loadMenu(): void {
    this.menuService.getMenu().subscribe({
      next: (data) => {
        this.menuCategories.set(data);
        if (data.length > 0) {
          this.selectedCategory = data[0].categoriaId;
        }
      },
      error: (err) => console.error('Error cargando el menú', err)
    });
  }

  obtenerIdsParaComanda(): number[] {
    const ids: number[] = [];
    this.orderService.order.forEach(item => {
      for (let i = 0; i < item.quantity; i++) {
        ids.push(item.product.id);
      }
    });
    return ids;
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

  navigateToPedido(): void {
    this.router.navigate(['/pedido']);
  }
}
