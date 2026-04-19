export interface ProductoDTO {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  imagen: string;
}

export interface ItemComandaDTO {
  id: number;
  productName: string;
  quantity: number;
  status: string;
  // Con extras o sin ...
  observations?: string;
  // Lo que escribe el cliente
  itemNotes?: string;
}

export interface ComandaResponseDTO {
  id: number;
  tableNumber: number;
  status: string;
  items: ItemComandaDTO[];
  isExiting?: boolean;
}
