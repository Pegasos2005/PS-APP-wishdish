import { Product } from '../menu/product-card/product.interface';

export interface OrderItem {
  product: Product;
  quantity: number;
}
