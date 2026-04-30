import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// RUTA HACIA EL SERVICIO (Esta funcionaba bien)
import { ProductService } from '../../../../core/services/product.service';

// RUTA HACIA LA INTERFAZ
// Si tu carpeta interfaces está en src/app/admin/interfaces/ usa esta:
import { ProductDTO } from '../../interfaces/product.interface';
// Si tu carpeta interfaces está en src/app/features/admin/interfaces/
// borra la línea de arriba y descomenta la de abajo:
// import { ProductDTO } from '../../interfaces/product.interface';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private router = inject(Router);

  products = signal<ProductDTO[]>([]);

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
    this.router.navigate(['/admin/products/new']);
  }

  editProduct(product: ProductDTO) {
    this.router.navigate(['/admin/products/edit', product.id]);
  }

  deleteProduct(product: ProductDTO) {
    if (confirm(`¿Estás seguro de que quieres borrar ${product.name}?`)) {
      this.productService.deleteProduct(product.id!).subscribe(() => {
        this.products.update(current => current.filter(p => p.id !== product.id));
      });
    }
  }
}
