import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customer-dish-detail',
  imports: [CommonModule],
  templateUrl: './customer-dish-detail.component.html',
  styleUrl: './customer-dish-detail.component.css',
})
export class CustomerDishDetailComponent implements OnInit {
  @Input() product: any;
  @Output() close = new EventEmitter<void>();
  @Output() addToCart = new EventEmitter<any>();

  // Ingredientes seleccionados
  selectedIngredients: Set<string> = new Set();

  ngOnInit() {

    if (this.product?.ingredients) {
      // Solo marcamos por defecto los que tengan isDefault === true en Java
      this.product.ingredients.forEach((ing: any) => {

        // Busca isDefault, pero si le ha quitado el 'is' y lo llama 'default', le decimos que lo acepte tambien"
        if (ing.isDefault === true || ing.default === true || String(ing.isDefault) === 'true') {
          this.selectedIngredients.add(ing.id || ing.name);
        }
      });
    }
  }

  toggleIngredient(ingredientId: string) {
    if (this.selectedIngredients.has(ingredientId)) {
      this.selectedIngredients.delete(ingredientId);
    } else {
      this.selectedIngredients.add(ingredientId);
    }
  }

  /**
   * Verifica si un ingrediente está seleccionado
   */
  isIngredientSelected(ingredientId: string): boolean {
    return this.selectedIngredients.has(ingredientId);
  }

  /**
   * Confirmar y añadir al carrito
   */
  confirmAddToCart() {
    const itemWithIngredients = {
      ...this.product,
      selectedIngredients: Array.from(this.selectedIngredients)
    };

    this.addToCart.emit(itemWithIngredients);
    this.closeModal();
  }

  /**
   * Cerrar modal
   */
  closeModal() {
    this.close.emit();
  }
}
