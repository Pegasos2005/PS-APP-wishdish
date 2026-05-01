import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto.model';
import { OrderItem } from '../interfaces/order-item.interface'; // Fíjate en el nuevo nombre
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CustomerOrderService {
  private apiUrl = environment.apiUrl + 'orders';

  tableId = signal<number | null>(null);

  order: OrderItem[] = [];
  private _totalItems = signal(0);
  totalItems = computed(() => this._totalItems());

  constructor(private http: HttpClient) {
    const stored = sessionStorage.getItem('tableId');
    if (stored) this.tableId.set(+stored);
  }

  setTableId(id: number) {
    this.tableId.set(id);
    sessionStorage.setItem('tableId', String(id));
  }

  checkTableExists(tableNumber: number): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiUrl}tables/${tableNumber}/exists`);
  }

  getTicketByTable(tableId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/table/${tableId}`);
  }

  requestPayment(tableNumber: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}tables/${tableNumber}/request-payment`, {});
  }

  getTableStatus(tableNumber: number): Observable<{ paymentRequested: boolean; hasActiveOrders: boolean }> {
    return this.http.get<{ paymentRequested: boolean; hasActiveOrders: boolean }>(
      `${environment.apiUrl}tables/${tableNumber}/status`
    );
  }

  closeTable(tableNumber: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}tables/${tableNumber}/close`, {});
  }

  getTablesAwaitingPayment(): Observable<number[]> {
    return this.http.get<number[]>(`${environment.apiUrl}tables/payment-requested`);
  }

  // 1. EL BOTÓN DE PAGAR (Envía a la base de datos)
  crearPedido(pedido: any): Observable<any> {
    return this.http.post(this.apiUrl, pedido);
  }

  // Funcion auxiliar: convierte la lista de ingredientes en un texto ordenado para poder comparar
  private getIngredientsSignature(product: any): string {
    const extras = product.addedExtras ? [...product.addedExtras].sort().join('|') : '';
    const removed = product.removedDefaults ? [...product.removedDefaults].sort().join('|') : '';
    return extras + '###' + removed;
  }

  // 2. GESTIÓN DEL CARRITO EN MEMORIA
  addProduct(productToAdd: any) {

    const signatureToAdd = this.getIngredientsSignature(productToAdd);

    const existingItem = this.order.find(item =>
      item.product.id === productToAdd.id &&
      this.getIngredientsSignature(item.product) === signatureToAdd
    );

    if (existingItem) {
      existingItem.quantity++;
    } else {
      // hacemos copia profunda del producto para evitar referencias cruzadas
      const newProduct = JSON.parse(JSON.stringify(productToAdd));
      this.order.push({ product: newProduct, quantity: 1 });
    }
    this._totalItems.update(val => val + 1);
  }

  decreaseProduct(productToRemove: any) {

    const signatureToRemove = this.getIngredientsSignature(productToRemove);

    const index = this.order.findIndex(item =>
      item.product.id === productToRemove.id && this.getIngredientsSignature(item.product) === signatureToRemove
    );

    if (index !== -1) {
      this.order[index].quantity--;
      this._totalItems.update(val => val - 1);
      if (this.order[index].quantity === 0) {
        this.order.splice(index, 1);
      }
    }
  }

  clear() {
    this.order = [];
    this._totalItems.set(0);
  }

  getSelectedProducts(): OrderItem[] {
    return this.order;
  }

  getTotalPrice(): number {
    return this.order.reduce((total, item) => {
      // Cogemos el precio con extras (si lo hay) o el precio base
      const priceToUse = item.product.calculatedPrice || item.product.price;

      // Lo multiplicamos por la cantidad de ese mismo plato y lo sumamos al total
      return total + (Number(priceToUse) * item.quantity);
    }, 0);
  }

}
