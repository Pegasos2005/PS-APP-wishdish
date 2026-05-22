import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { ProductDTO } from '../../../../core/interfaces/product.interface';
import { ProductService } from '../../../../core/services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink], // Ya no necesitamos FormsModule
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private router = inject(Router);

  // Lista base
  products = signal<ProductDTO[]>([]);

  // Signal para el término de búsqueda
  searchTerm = signal('');

  // Signal computado: Se actualiza automáticamente cuando 'products' o 'searchTerm' cambian
  filteredProducts = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.products().filter(product =>
      product.name.toLowerCase().includes(term)
    );
  });

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getProducts().subscribe({
      next: (data) => this.products.set(data),
      error: (err) => console.error('Error al cargar productos:', err)
    });
  }

  addNewProduct() {
    this.router.navigate(['/admin/product-management/product-form']);
  }

  editProduct(product: ProductDTO) {
    this.router.navigate(['/admin/product-management/product-form', product.id]);
  }

  toggleAvailability(product: ProductDTO) {
    if (!product.id) return;
    
    const updatedProduct = { ...product, available: !product.available };
    
    this.productService.updateProduct(product.id, updatedProduct).subscribe({
      next: () => {
        this.products.update(current => 
          current.map(p => p.id === product.id ? { ...p, available: !p.available } : p)
        );
      },
      error: (err) => console.error('Error updating availability:', err)
    });
  }

  deleteProduct(product: ProductDTO) {
    // 1. Guard clause: evita borrar si no hay ID
    if (!product.id) {
      console.error('El producto no tiene ID');
      return;
    }

    if (confirm(`¿Estás seguro de que quieres borrar ${product.name}?`)) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          // Esto solo se ejecuta si la petición al servidor tuvo éxito (2xx)
          this.products.update(current => current.filter(p => p.id !== product.id));
          console.log('Producto borrado con éxito');
        },
        error: (err) => {
          // ESTO ES LO QUE TE FALTA: Si falla, verás el error aquí
          console.error('Error al intentar borrar el producto:', err);
          alert('No se pudo borrar el producto. Revisa la consola (F12).');
        }
      });
    }
  }
}
