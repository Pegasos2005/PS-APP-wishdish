import { Producto } from '../models/producto.model';

export interface OrderItem {
  product: Producto;
  quantity: number;
}