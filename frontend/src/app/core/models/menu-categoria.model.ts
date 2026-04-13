import { Producto } from './producto.model';

/**
 * Interface MenuCategoria (respuesta del endpoint /api/menu)
 * Coincide con MenuCategoryDTO.java del backend
 */
export interface MenuCategoria {
  categoryId: number;
  categoryName: string;
  categoryDescription: string;
  products: Producto[];
}
