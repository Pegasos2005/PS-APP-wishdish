import { Component, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';

@Component({
  selector: 'app-ingredient-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ingredient-form.component.html',
  styleUrls: ['./ingredient-form.component.css']
})
export class IngredientFormComponent {
  private fb = inject(FormBuilder);
  private productService = inject(ProductService);
  private location = inject(Location);
  private router = inject(Router);

  ingredientForm = this.fb.group({
    name: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]]
  });

  goBack() { this.location.back(); }

  onSubmit() {
    if (this.ingredientForm.valid) {
      this.productService.createIngredient(this.ingredientForm.value as any).subscribe(() => {
        this.router.navigate(['/admin/products']);
      });
    }
  }
}
