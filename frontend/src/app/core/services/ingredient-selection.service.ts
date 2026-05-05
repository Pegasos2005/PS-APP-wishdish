import { Injectable } from '@angular/core';
import { Ingredient } from '../interfaces/ingredient.interface';

@Injectable({ providedIn: 'root' })
export class IngredientSelectionService {
  private selected: Ingredient[] = [];
  private productDraft: any = null;

  setSelection(ingredients: Ingredient[]) {
    this.selected = ingredients;
  }

  getSelection(): Ingredient[] {
    return this.selected;
  }

  hasPendingChanges(): boolean {
    return this.selected.length > 0;
  }

  setDraft(data: any) {
    this.productDraft = data;
  }

  getDraft() {
    return this.productDraft;
  }

  clear() {
    this.selected = [];
    this.productDraft = null;
  }
}
