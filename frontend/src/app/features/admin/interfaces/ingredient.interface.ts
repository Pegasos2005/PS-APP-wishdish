export interface Ingredient {
  id: number;
  name: string;
  description?: string; // Opcional, por si lo añades más tarde
  extraPrice: number;   // BigDecimal en Java -> number en TS
}
