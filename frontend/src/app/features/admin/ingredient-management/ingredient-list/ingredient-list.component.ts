import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { Ingredient } from '../../../../core/interfaces/ingredient.interface';
import { IngredientService } from '../../../../core/services/ingredient.service';

@Component({
  selector: 'app-ingredient-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './ingredient-list.component.html',
  styleUrls: ['./ingredient-list.component.css']
})
export class IngredientListComponent implements OnInit {
  private ingredientService = inject(IngredientService);
  private router = inject(Router);

  // Lista base
  ingredients = signal<Ingredient[]>([]);

  // Signal para el término de búsqueda
  searchTerm = signal('');

  // Signal computado: Filtra la lista automáticamente
  filteredIngredients = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.ingredients().filter(ingredient =>
      ingredient.name.toLowerCase().includes(term)
    );
  });

  ngOnInit() {
    this.loadIngredients();
  }

  loadIngredients() {
    this.ingredientService.getIngredients().subscribe({
      next: (data: Ingredient[]) => this.ingredients.set(data),
      error: (err) => console.error('Error al cargar ingredientes:', err)
    });
  }

  addNewIngredient() {
    this.router.navigate(['/admin/ingredient-management/ingredient-form']);
  }

  editIngredient(ingredient: Ingredient) {
    this.router.navigate(['/admin/ingredient-management/ingredient-form', ingredient.id]);
  }

  toggleAvailability(ingredient: Ingredient) {
    if (!ingredient.id) return;
    
    const updatedIngredient = { ...ingredient, available: !ingredient.available };
    
    this.ingredientService.updateIngredient(ingredient.id, updatedIngredient).subscribe({
      next: () => {
        this.ingredients.update(current => 
          current.map(i => i.id === ingredient.id ? { ...i, available: !i.available } : i)
        );
      },
      error: (err) => console.error('Error toggling ingredient availability:', err)
    });
  }

  deleteIngredient(ingredient: Ingredient) {
    console.log("ID a borrar:", ingredient.id); // <--- MIRA ESTO EN LA CONSOLA

    if (confirm(`¿Estás seguro de que quieres eliminar ${ingredient.name}?`)) {
      // El servicio ahora ejecutará un DELETE real
      this.ingredientService.deleteIngredient(ingredient.id!).subscribe({
        next: () => {
          // Al actualizar el signal, la UI se refresca sola eliminándolo de la lista
          this.ingredients.update(current => current.filter(i => i.id !== ingredient.id));
          console.log('Ingrediente eliminado correctamente');
        },
        error: (err) => {
          console.error('Error:', err);
          // Cambiamos el mensaje para que el usuario sepa qué pasó
          alert('No se pudo eliminar el ingrediente.');
        }
      });
    }
  }
}
