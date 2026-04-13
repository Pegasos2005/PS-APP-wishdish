/**
 * Interface de Producto (coincide con el backend)
 */
export interface Producto {
  id: number;
  name: string;
  description: string;
  price: number;
  picture: string;
  available: boolean;
  categoria?: Categoria;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

/**
 * Interface de Categoría (coincide con el backend)
 */
export interface Categoria {
  id: number;
  name: string;
  description: string;
}
