// src/app/admin/interfaces/product.interface.ts

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
  productIngredients: ProductIngredient[];
}

export interface ProductIngredient {
  id: number;
  isDefault: boolean;
  ingredient: {
    id: number;
    name: string;
    extraPrice: number;
  };
}
