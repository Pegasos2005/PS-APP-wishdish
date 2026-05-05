// src/app/admin/interfaces/product.interface.ts
import { Category } from './category.interface';

// Interfaz ligera para listados
export interface ProductDTO {
  id?: number;
  name: string;
  price: number;
  description?: string;
  picture?: string;
}

// Interfaz completa para edición
export interface Product extends ProductDTO {
  // CAMBIO: Ahora coincide con el nombre de la lista en tu Java DTO (ingredients)
  ingredients: IngredientDTO[];
  // Añadimos la categoría
  category?: Category;
}

// Nueva estructura que refleja el DTO de Java
export interface IngredientDTO {
  id: number;
  name: string;
  extraPrice: number;
  isDefault: boolean; // Este valor viene mapeado desde la tabla intermedia
}
