// src/app/admin/interfaces/product.interface.ts
import { Category } from './category.interface';

// Interfaz que refleja el ProductDTO de Java, usada para listados y edición
export interface ProductDTO {
  id?: number;
  name: string;
  price: number;
  description?: string;
  picture?: string;
  available: boolean;
  ingredients: IngredientDTO[]; // Coincide con el DTO de Java
  category?: Category; // Coincide con el DTO de Java
}

// Interfaz completa para edición
// Si ProductDTO ya es completa, la interfaz Product puede ser un alias o eliminada si no añade nada más.
// Por ahora, la haremos un alias para mantener la compatibilidad.
export type Product = ProductDTO;

// Estructura que refleja el IngredientDTO de Java, usada dentro de ProductDTO
export interface IngredientDTO {
  id: number;
  name: string;
  description?: string; // Añadido para coincidir con Java IngredientDTO
  extraPrice: number;
  isDefault: boolean; // Este valor viene mapeado desde la tabla intermedia
  available: boolean; // Añadido para coincidir con Java IngredientDTO
}
