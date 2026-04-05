export interface ProductoDTO {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  imagen: string;
}

export interface ItemComandaDTO {
  id: number;
  producto: ProductoDTO;
  cantidad: number;
  estado: string;
  notasItem?: string;
}

export interface ComandaResponseDTO {
  id: number;
  numeroMesa: number;
  fechaComanda: string;
  estado: string;
  notasGenerales?: string;
  items: ItemComandaDTO[];
}
