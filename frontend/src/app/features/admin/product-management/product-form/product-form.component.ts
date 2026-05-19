import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { IngredientSelectionService } from '../../../../core/services/ingredient-selection.service';
import { CategoryService } from '../../../../core/services/category.service';
import { Category } from '../../../../core/interfaces/category.interface';

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
  private categoryService = inject(CategoryService);
  private selectionService = inject(IngredientSelectionService);

  productForm: FormGroup;
  isEditMode = false;
  productId: number | null = null;
  categories: Category[] = [];

  constructor() {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      description: [''],
      categoryId: [null, Validators.required],
      picture: [''],
      productIngredients: this.fb.array([])
    });
  }

  get ingredientsArray(): FormArray {
    return this.productForm.get('productIngredients') as FormArray;
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!(id && id !== 'null');
    this.productId = this.isEditMode ? Number(id) : null;

    // Load categories first, then proceed based on edit mode or draft
    this.loadCategoriesAndProduct();
  }

  /**
   * Loads categories and then, based on the component's state (edit mode or draft),
   * either loads product data from the DB or applies a draft.
   */
  loadCategoriesAndProduct() {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;

        // Now that categories are loaded, apply product data
        if (this.selectionService.getDraft()) {
          // If there's a draft (returned from picker), use it
          this.productForm.patchValue(this.selectionService.getDraft());
          this.populateIngredients(this.selectionService.getSelection());
        } else if (this.isEditMode && this.productId) {
          // If in edit mode and no draft, load from DB
          this.loadProduct(this.productId);
        }
      },
      error: (err) => console.error('Error cargando categorías:', err)
    });
  }
  
  loadProduct(id: number) {
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          name: product.name,
          description: product.description,
          price: product.price,
          categoryId: product.category ? Number(product.category.id || product.category) : null,
          picture: product.picture
        });

        // PRIORIDAD: Si venimos del picker, usamos lo que hay en el service.
        // Si no, cargamos lo que viene de la DB.
        if (this.selectionService.hasPendingChanges()) {
          this.populateIngredients(this.selectionService.getSelection());
        } else if (product.ingredients && Array.isArray(product.ingredients)) {
          this.populateIngredients(product.ingredients);
        }
      },
      error: (err) => console.error('Error cargando producto:', err)
    });
  }

  populateIngredients(ingredients: any[]) {
    const array = this.ingredientsArray;
    array.clear();
    ingredients.forEach(ing => {
      // El ingrediente puede venir como objeto directo o anidado según el origen
      array.push(this.fb.group({
        id: [ing.id || ing.ingredient?.id],
        name: [ing.name],
        extraPrice: [ing.extraPrice],
        isDefault: [ing.isDefault !== undefined ? ing.isDefault : true]
      }));
    });
  }

  openIngredientPicker() {
    // Guardar el estado actual del formulario (nombre, precio, etc.)
    const formValue = { ...this.productForm.value };
    delete formValue.productIngredients; // No duplicamos los ingredientes
    this.selectionService.setDraft(formValue);

    // Guardar los ingredientes actuales
    this.selectionService.setSelection(this.ingredientsArray.value);

    if (this.isEditMode && this.productId) {
      this.router.navigate(['/admin/product-management/ingredient-picker', this.productId]);
    } else {
      this.router.navigate(['/admin/product-management/ingredient-picker']);
    }
  }

  onSubmit() {
    if (this.productForm.invalid) {
      alert('Por favor, rellena todos los campos obligatorios.');
      return;
    }

    // --- TRANSFORMACIÓN DE DATOS PARA EL BACKEND ---
    // Esto convierte el array plano del formulario en el objeto {ingredient: {id: X}} que JPA espera
    const { categoryId, productIngredients, ...productData } = this.productForm.value;

    const formattedIngredients = this.ingredientsArray.value.map((item: any) => ({
        ingredient: { id: item.id }, // <--- EL BACKEND NECESITA ESTA ESTRUCTURA
        isDefault: item.isDefault || false
    }));

    // Estructura de categoría para JPA
    const category = categoryId ? { id: Number(categoryId) } : null;

    const payload = {
        ...productData,
        category: category,
        productIngredients: formattedIngredients
    };
    // -------------------------------------------------

    this.selectionService.clear();

    if (this.isEditMode && this.productId) {
      this.productService.updateProduct(this.productId, payload).subscribe({
      next: () => {
                 this.selectionService.clear();
                 this.router.navigate(['/admin/product-management/product-list']);
        },
        error: (err) => console.error("Error al actualizar", err)
      });
    } else {
      this.productService.createProduct(payload).subscribe({
      next: () => {
                 this.selectionService.clear();
                 this.router.navigate(['/admin/product-management/product-list']);
        },
        error: (err) => console.error("Error al crear", err)
      });
    }
  }

  goBack() {
    this.selectionService.clear();
    this.router.navigate(['/admin/product-management/product-list']);
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      // Llamamos al backend para que guarde la foto física
      this.productService.uploadImage(file).subscribe({
        next: (response) => {
          // El backend nos devuelve la URL y la guardamos en el formulario
          this.productForm.patchValue({ picture: response.imageUrl });
        },
        error: (err) => console.error('Error al subir imagen', err)
      });
    }
  }
}
