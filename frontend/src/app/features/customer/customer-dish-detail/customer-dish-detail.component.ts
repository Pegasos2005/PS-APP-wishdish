import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-customer-dish-detail',
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-dish-detail.component.html',
  styleUrl: './customer-dish-detail.component.css',
})
export class CustomerDishDetailComponent implements OnInit {
  @Input() product: any;
  @Output() close = new EventEmitter<void>();
  @Output() addToCart = new EventEmitter<any>();

  // Ingredientes seleccionados
  selectedIngredients: Set<string> = new Set();
  itemNotesText: string = '';

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

  // Para saber si un ingrediente es por defecto
  isDefaultIngredient(ing: any): boolean {
    return ing.isDefault === true || ing.default === true || String(ing.isDefault) === 'true';
  }

  /**
   * Calcula el precio en tiempo real (Base + Extras seleccionados)
   */
  calcularPrecioTotal(): number {
    let total = Number(this.product?.price) || 0;

    if (this.product?.ingredients) {
      this.product.ingredients.forEach((ing: any) => {
        // Si ESTÁ seleccionado y NO es por defecto, sumamos su precio extra
        if (this.isIngredientSelected(ing.id || ing.name) && !this.isDefaultIngredient(ing)) {
          total += Number(ing.extraPrice) || 0;
        }
      });
    }

    return total;
  }

  /**
   * Confirmar y añadir al carrito
   */
  confirmAddToCart() {

    const addedExtras: string[] = [];
    const removedDefaults: string[] = [];

    if (this.product?.ingredients) {
      this.product.ingredients.forEach((ing: any) => {
        const isSelected = this.selectedIngredients.has(ing.id || ing.name);
        const isDefault = this.isDefaultIngredient(ing);

        if (isDefault && !isSelected) {
          // Venía por defecto pero el cliente lo quitó
          removedDefaults.push(ing.name);
        } else if (!isDefault && isSelected) {
          // Era un extra y el cliente lo añadió
          addedExtras.push(ing.name);
        }
      });
    }

    const itemWithIngredients = {
      ...this.product,
      calculatedPrice: this.calcularPrecioTotal(),
      addedExtras: addedExtras,
      removedDefaults: removedDefaults,
      itemNotes: this.itemNotesText.trim()
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
