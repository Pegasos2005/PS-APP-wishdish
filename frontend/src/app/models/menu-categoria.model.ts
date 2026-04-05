import { Producto } from './producto.model';

/**
 * Interface MenuCategoria (respuesta del endpoint /api/menu)
 * Coincide con MenuCategoriaDTO.java del backend
 */
export interface MenuCategoria {
  categoriaId: number;
  categoriaNombre: string;
  categoriaDescripcion: string;
  productos: Producto[];
}
