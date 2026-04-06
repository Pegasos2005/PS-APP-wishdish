import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductCard } from './product-card/product-card';
import { MenuService } from '../services/menu.service';
import { MenuCategoria } from '../models/menu-categoria.model';
import { ComandaService } from '../services/comanda.service';
import { OrderService } from '../services/order';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, ProductCard, RouterLink],
  templateUrl: './menu.html',
  styleUrl: './menu.css',
})
export class Menu implements OnInit {
  menuCategories = signal<MenuCategoria[]>([]);
  selectedCategory: number | null = null;
  isLoading = signal<boolean>(false);

  constructor(
    private menuService: MenuService,
    private comandaService: ComandaService,
    public orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMenu();
  }

  private loadMenu(): void {
    this.isLoading.set(true);

    this.menuService.getMenu().subscribe({
      next: (data) => {
        this.menuCategories.set(data);
        this.isLoading.set(false);
        console.log('Menú cargado correctamente', data);
      },
      error: (error) => {
        console.error('Error al cargar el menú:', error);
        this.isLoading.set(false);
      }
    });
  }

  sendOrder() {
    if (this.orderService.totalItems() === 0) {
      alert('El carrito está vacío');
      return;
    }

    const productosIds = this.orderService.order.flatMap(item =>
      Array(item.quantity).fill(item.product.id)
    );

    const comandaRequest = {
      numeroMesa: 1,
      productosIds
    };

    this.comandaService.crearComanda(comandaRequest).subscribe({
      next: (comanda: any) => {
        console.log('Comanda creada:', comanda);
        alert(`Comanda creada exitosamente para mesa ${comandaRequest.numeroMesa}`);
        this.orderService.clear();
      },
      error: (error: any) => {
        console.error('Error al crear comanda:', error);

        let errorMessage = 'Error al crear la comanda';
        if (error.status === 0) {
          errorMessage = 'No se puede conectar con el servidor. Verifica que el backend esté corriendo en http://localhost:8080';
        } else if (error.error?.message) {
          errorMessage = `Error: ${error.error.message}`;
        } else if (error.message) {
          errorMessage = error.message;
        }

        alert(errorMessage);
      }
    });
  }

  navigateToPedido() {
    this.router.navigate(['/pedido']);
  }

  scrollToCategory(categoryId: number) {
    this.selectedCategory = categoryId;
    const element = document.querySelector(
      `[data-category-id="${categoryId}"]`
    ) as HTMLElement;

    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }

  onScroll(event: Event) {
    const container = event.target as HTMLElement;
    const categories = document.querySelectorAll('.section-title');

    for (let section of categories) {
      const rect = section.getBoundingClientRect();
      if (rect.top < 300) {
        const categoryId = (section as HTMLElement).getAttribute('data-category-id');
        if (categoryId) {
          this.selectedCategory = parseInt(categoryId);
        }
      }
    }
  }
}
