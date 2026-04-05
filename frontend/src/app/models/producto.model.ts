/**
 * Interface de Producto (coincide con el backend)
 */
export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  imagen: string;
  disponible: boolean;
  categoria?: Categoria;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

/**
 * Interface de Categoría (coincide con el backend)
 */
export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string;
}
