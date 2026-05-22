export interface Ingredient {
  id?: number; // Opcional: así no tendrás errores al crear un objeto nuevo
  name: string;
  description?: string;
  extraPrice: number;
  available: boolean;
}
