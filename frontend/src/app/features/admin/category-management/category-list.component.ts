import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { Category } from '../../../core/interfaces/category.interface'; // Usar la interfaz correcta

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryManagementComponent implements OnInit {

  private categoryService = inject(CategoryService);
  private router = inject(Router);

  categories = signal<Category[]>([]);
  searchTerm = signal('');

  filteredCategories = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.categories().filter(c => c.name.toLowerCase().includes(term));
  });

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe(data => this.categories.set(data));
  }

  addNewCategory() {
    this.router.navigate(['/admin/category-management/new']);
  }

  editCategory(category: Category) {
    this.router.navigate(['/admin/category-management/edit', category.id]);
  }

  delete(id?: number) {
    if (id && confirm('¿Estás seguro de que quieres eliminar esta categoría?')) {
      this.categoryService.deleteCategory(id).subscribe(() => {
        this.loadCategories();
      });
    }
  }
}