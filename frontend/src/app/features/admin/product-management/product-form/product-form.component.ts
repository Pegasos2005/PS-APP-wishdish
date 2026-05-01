import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private productService = inject(ProductService);

  productForm: FormGroup;
  isEditMode = false;
  productId: number | null = null;
  currentProduct: any = null; // Para guardar el producto y mostrar su imagen

  constructor() {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      description: [''],
      picture: [''],
      productIngredients: this.fb.array([]) // Array para ingredientes
    });
  }

  get ingredientsArray(): FormArray {
    return this.productForm.get('productIngredients') as FormArray;
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.productId = Number(id);
      this.loadProduct(this.productId);
    }
  }

  loadProduct(id: number) {
    this.productService.getProductById(id).subscribe(product => {
      this.currentProduct = product; // Guardamos el objeto para la imagen

      // Rellenar campos básicos
      this.productForm.patchValue({
        name: product.name,
        price: product.price,
        description: product.description,
        picture: product.picture
      });

      // Limpiar y rellenar ingredientes
      this.ingredientsArray.clear();
      product.productIngredients?.forEach((pi: any) => {
        this.ingredientsArray.push(this.fb.group({
          id: [pi.id],
          isDefault: [pi.isDefault],
          ingredient: [pi.ingredient]
        }));
      });
    });
  }

  onSubmit() {
    if (this.productForm.invalid) return;

    // Si estamos en modo edición (que lo sabemos porque productId no es null)
    if (this.isEditMode && this.productId) {
      this.productService.updateProduct(this.productId, this.productForm.value)
        .subscribe({
          next: () => {
            console.log("Producto actualizado con éxito");
            // Redirigir a la lista de productos
            this.router.navigate(['/admin/products']); // Ajusta esta ruta si tu lista tiene otra
          },
          error: (err) => console.error("Error al actualizar", err)
        });
    }
  }

  goBack() { this.location.back(); }
}
