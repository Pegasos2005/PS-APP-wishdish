import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryService } from '../../../../core/services/category.service';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private categoryService = inject(CategoryService);

  categoryForm: FormGroup;
  isEditMode = false;
  categoryId: number | null = null;

  constructor() {
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['']
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.categoryId = Number(id);
      this.loadCategory(this.categoryId);
    }
  }

  loadCategory(id: number) {
    this.categoryService.getCategoryById(id).subscribe({
      next: (category) => {
        this.categoryForm.patchValue({
          name: category.name,
          description: category.description
        });
      },
      error: (err) => console.error('Error al cargar categoría', err)
    });
  }

  onSubmit() {
    if (this.categoryForm.invalid) return;

    const payload = this.categoryForm.value;

    if (this.isEditMode && this.categoryId) {
      this.categoryService.updateCategory(this.categoryId, payload).subscribe({
        next: () => {
          alert('Categoría actualizada exitosamente.');
          this.router.navigate(['/admin/category-management']);
        },
        error: (err) => {
          console.error('Error al actualizar la categoría:', err);
          alert(err.error?.message || 'Error al actualizar la categoría. Revisa la consola para más detalles.');
        }
      });
    } else {
      this.categoryService.createCategory(payload).subscribe({
        next: () => {
          alert('Categoría creada exitosamente.');
          this.router.navigate(['/admin/category-management']);
        },
        error: (err) => {
          console.error('Error al crear la categoría:', err);
          alert(err.error?.message || 'Error al crear la categoría. Revisa la consola para más detalles.');
        }
      });
    }
  }
  goBack() {
    this.location.back();
  }
}
