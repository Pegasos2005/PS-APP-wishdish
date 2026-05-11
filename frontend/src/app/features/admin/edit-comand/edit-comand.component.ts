import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../environments/environment';
import { ComandaResponseDTO } from '../../../core/models/comanda.model';

// Interfaz para el catálogo de productos
export interface ProductCatalogItem {
  id: number;
  name: string;
  price: number;
  picture: string;
  available: boolean;
}

@Component({
  selector: 'app-edit-comand',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-comand.component.html',
  styleUrls: ['./edit-comand.component.css']
})
export class EditComandComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // Datos de la comanda actual
  orderId = signal<number>(0);
  currentOrder = signal<ComandaResponseDTO | null>(null);
  loadingOrder = signal<boolean>(true);

  // Catálogo y Búsqueda
  products = signal<ProductCatalogItem[]>([]);
  searchTerm = signal<string>('');

  // Formulario para añadir manualmente
  selectedProduct = signal<ProductCatalogItem | null>(null);
  quantity = signal<number>(1);
  observations = signal<string>('');

  // Filtra los productos en tiempo real al teclear
  filteredProducts = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    return this.products().filter(p => p.name.toLowerCase().includes(term));
  });

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.orderId.set(Number(idParam));
      this.loadOrderDetails();
      this.loadCatalog();
    }
  }

  // Carga la comanda buscando su ID dentro de las activas
  loadOrderDetails() {
    this.loadingOrder.set(true);
    this.http.get<ComandaResponseDTO[]>(`${environment.apiUrl}orders/active`).subscribe({
      next: (orders) => {
        const found = orders.find(o => o.id === this.orderId()) || null;
        this.currentOrder.set(found);
        this.loadingOrder.set(false);
      },
      error: (err) => {
        console.error('Error al cargar los detalles de la comanda:', err);
        this.loadingOrder.set(false);
      }
    });
  }

  // Carga todo el catálogo disponible
  loadCatalog() {
    this.http.get<any[]>(`${environment.apiUrl}products`).subscribe({
      next: (data) => {
        // Mapeo seguro adaptándose a inglés/español
        const mappedCatalog: ProductCatalogItem[] = data.map(item => ({
          id: item.id,
          name: item.name || item.nombre || 'Producto sin nombre',
          price: item.price || item.precio || 0,
          picture: item.picture || item.imagen || 'assets/placeholder.png',
          available: item.available !== undefined ? item.available : true
        }));
        this.products.set(mappedCatalog);
      },
      error: (err) => console.error('Error al cargar el catálogo de productos:', err)
    });
  }

  // Seleccionar un producto de la lista rápida
  selectProduct(prod: ProductCatalogItem) {
    this.selectedProduct.set(prod);
    this.quantity.set(1); // Reiniciamos cantidad por defecto
  }

  addManualItem() {
    const prod = this.selectedProduct();
    if (!prod || !this.orderId()) return;

    // Preparamos el paquete de datos para Java
    const payload = {
      productId: prod.id,
      quantity: this.quantity(),
      observations: this.observations().trim()
    };

    console.log('Sending manual addition:', payload);

    this.http.post<ComandaResponseDTO[]>(
      `${environment.apiUrl}orders/${this.orderId()}/items`,
      payload
    ).subscribe({
      next: (updatedOrders) => {
        // Buscamos nuestra comanda en la lista actualizada que devuelve el servidor
        const found = updatedOrders.find(o => o.id === this.orderId()) || null;
        this.currentOrder.set(found);

        // Limpiamos el formulario para permitir añadir otro producto rápido
        this.selectedProduct.set(null);
        this.quantity.set(1);
        this.observations.set('');
        this.searchTerm.set('');

        alert('¡Producto añadido y enviado a cocina con éxito!');
      },
      error: (err) => {
        console.error('Error en la adición manual:', err);

        // Si el backend devuelve un 400 con el mensaje de disponibilidad
        if (err.status === 400 && typeof err.error === 'string') {
          alert(err.error); // Mostrará: "Este producto no está disponible actualmente"
        } else {
          alert('Hubo un error al intentar añadir el producto.');
        }
      }
    });
  }

  isStaffAdded(item: any): boolean {
    if (!item || !item.observations) return false;
    return item.observations.includes('[Añadido por personal]');
  }

  goBack() {
    this.router.navigate(['/admin/edit-comand-select']);
  }

  removeItem(itemId: number) {
    if (!confirm('¿Estás seguro de que deseas eliminar este producto de la comanda?')) return;

    this.http.delete(`${environment.apiUrl}orders/items/${itemId}`).subscribe({
      next: () => {
        // Actualizamos la vista local eliminando el ítem de la lista
        if (this.currentOrder()) {
          const updatedItems = this.currentOrder()!.items.filter(i => i.id !== itemId);
          this.currentOrder.set({ ...this.currentOrder()!, items: updatedItems });
        }
        console.log('Ítem eliminado correctamente');
      },
      error: (err) => {
        console.error('Error al eliminar ítem:', err);
        alert('No se pudo eliminar el producto.');
      }
    });
  }
}
